/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.ae.storage.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openscada.ae.core.Event;
import org.openscada.ae.core.ListOperationListener;
import org.openscada.ae.core.Listener;
import org.openscada.ae.core.NoSuchQueryException;
import org.openscada.ae.core.QueryDescription;
import org.openscada.ae.core.Session;
import org.openscada.ae.core.Storage;
import org.openscada.ae.storage.common.operations.ListOperation;
import org.openscada.core.CancellationNotSupportedException;
import org.openscada.core.InvalidSessionException;
import org.openscada.utils.jobqueue.CancelNotSupportedException;
import org.openscada.utils.jobqueue.OperationManager;
import org.openscada.utils.jobqueue.OperationProcessor;
import org.openscada.utils.jobqueue.OperationManager.Handle;

public class StorageCommon implements Storage
{
    private static Logger _log = Logger.getLogger ( StorageCommon.class );
    
    private Map<String, QueryEntry> _queries = new HashMap<String, QueryEntry> ();
    
    private SubscriptionManager _subscriptionManager = new SubscriptionManager ();
    private OperationManager _opManager = new OperationManager ();
    private OperationProcessor _opProcessor = new OperationProcessor ();
    private Thread _opThread =  new Thread ( _opProcessor );
    
    public StorageCommon ()
    {
        super ();
        _opThread.setDaemon ( true );
        _opThread.start ();
    }
    
    /**
     * Validate a session and return the internal session object
     * @param session the session to validate
     * @return the validated session as internal session type
     * @throws InvalidSessionException thrown in the case the session is not valid for this storage
     */
    protected SessionCommon validateSession ( Session session ) throws InvalidSessionException
    {
        if ( session == null )
        {
            throw new InvalidSessionException ();
        }
        
        synchronized ( session )
        {
            if ( ! ( session instanceof SessionCommon ) )
            {
                throw new InvalidSessionException ();
            }

            SessionCommon sessionCommon = (SessionCommon)session;
            if ( sessionCommon.getStorage () != this )
            {
                throw new InvalidSessionException ();
            }

            return (SessionCommon)session;
        }
    }

    public void closeSession ( Session session ) throws InvalidSessionException
    {
        synchronized ( session )
        {
            SessionCommon sessionCommon = validateSession ( session );

            sessionCommon.invalidate ();
            
            // unsubscribe all the session has subscribed
            _subscriptionManager.unsubscribe ( sessionCommon );
        }
    }

    public Session createSession ( Properties properties )
    {
        return new SessionCommon ( this );
    }

    synchronized public long startList ( Session session, ListOperationListener listener ) throws InvalidSessionException
    {
        SessionCommon sessionCommon = validateSession ( session );
        
        Handle handle = _opManager.schedule ( new ListOperation ( this, listener ) );
        
        sessionCommon.getOperations ().addOperation ( handle );
        
        return handle.getId ();
    }
    
    synchronized public QueryDescription[] list ()
    {
        QueryDescription[] descriptions = new QueryDescription[_queries.size ()];
        int i = 0;
        for ( QueryEntry entry : _queries.values () )
        {
            descriptions[i] = new QueryDescription ( entry.getDescription () );
            i++;
        }
        return descriptions;
    }

    synchronized public Event[] read ( Session session, String queryID ) throws InvalidSessionException, NoSuchQueryException
    {
        @SuppressWarnings("unused")
        SessionCommon sessionCommon = validateSession ( session );
        
        QueryEntry queryEntry = _queries.get ( queryID );
        
        if ( queryEntry == null )
        {
            throw new NoSuchQueryException ();
        }
        
        Reader reader = queryEntry.getQuery ().createReader ();
        Event[] events = reader.fetchNext ( 0 );
        reader.close ();
        
        return events;
    }

    /**
     * Validate an event submission
     * 
     * The method must validate and approve the event for further processing. If the
     * event should not be processed an exception must the thrown indicating the error.
     * 
     * The default implementation does nothing.
     * 
     * Override if you need special handling here.
     * 
     * @param properties submission properties
     * @param event the submitted event
     * @throws Exception any exception that indicates an error condition
     */
    public void validateSubmission ( Properties properties, Event event ) throws Exception
    {
    }
    
    synchronized public void submitEvent ( Properties properties, Event event ) throws Exception
    {   
        validateSubmission ( properties, event );
        for ( QueryEntry queryEntry : _queries.values () )
        {
            queryEntry.getQuery ().submitEvent ( event );
        }
    }

    synchronized public void subscribe ( Session session, String queryID, Listener listener, int maxBatchSize, int archiveSet ) throws InvalidSessionException, NoSuchQueryException
    {
        SessionCommon sessionCommon = validateSession ( session );
        
        QueryEntry queryEntry = _queries.get ( queryID );
        
        if ( queryEntry == null )
            throw new NoSuchQueryException ();
        
        _subscriptionManager.subscribe ( sessionCommon, listener, queryEntry.getQuery (), maxBatchSize, archiveSet );
    }

    synchronized public void unsubscribe ( Session session, String queryID, Listener listener ) throws InvalidSessionException, NoSuchQueryException
    {
        SessionCommon sessionCommon = validateSession ( session );
        
        QueryEntry queryEntry = _queries.get ( queryID );
        
        if ( queryEntry == null )
            throw new NoSuchQueryException ();
        
        _subscriptionManager.unsubscribe ( queryEntry.getQuery (), sessionCommon, listener );
    }
    
    synchronized public void addQuery ( QueryDescription description, Query query )
    {
        if ( _queries.containsKey ( description.getId () ) )
            return;

        _queries.put ( description.getId (), new QueryEntry ( query, description ) );
    }
    
    synchronized public void removeQuery ( String name )
    {
        QueryEntry queryEntry;
        if ( ( queryEntry = _queries.remove ( name ) ) != null )
        {
            _subscriptionManager.unsubscribe ( queryEntry.getQuery () );
        }
    }

    public void cancelOperation ( Session session, long id ) throws CancellationNotSupportedException, InvalidSessionException
    {
        SessionCommon sessionCommon = validateSession ( session );
        
        synchronized ( sessionCommon )
        {
            _log.info ( String.format ( "Cancelling operation: %d", id ) );

            Handle handle = _opManager.get ( id );
            if ( handle != null )
            {
                if ( sessionCommon.getOperations ().containsOperation ( handle ) )
                {
                    try
                    {
                        handle.cancel ();
                    }
                    catch ( CancelNotSupportedException e )
                    {
                        throw new CancellationNotSupportedException ();
                    }
                }
            }
        }
    }

    public void thawOperation ( Session session, long id ) throws InvalidSessionException
    {
        SessionCommon sessionCommon = validateSession ( session );

        synchronized ( sessionCommon )
        {
            _log.info ( String.format ( "Thawing operation %d", id ) );

            Handle handle = _opManager.get ( id );
            if ( handle != null )
            {
                if ( sessionCommon.getOperations ().containsOperation ( handle ) )
                {
                    _opProcessor.add ( handle );
                }
            }
            else
                _log.warn ( String.format ( "%d is not a valid operation id", id ) );
        }
    }
}
