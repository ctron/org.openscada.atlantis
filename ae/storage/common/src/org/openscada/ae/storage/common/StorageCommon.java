package org.openscada.ae.storage.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openscada.ae.core.Event;
import org.openscada.ae.core.Listener;
import org.openscada.ae.core.NoSuchQueryException;
import org.openscada.ae.core.QueryDescription;
import org.openscada.ae.core.Session;
import org.openscada.ae.core.Storage;
import org.openscada.core.InvalidSessionException;

public class StorageCommon implements Storage
{
    private Map<String, QueryEntry> _queries = new HashMap<String, QueryEntry> ();
    
    private SubscriptionManager _subscriptionManager = new SubscriptionManager ();
    
    /**
     * Validate a session and return the internal session object
     * @param session the session to validate
     * @return the validated session as internal session type
     * @throws InvalidSessionException thrown in the case the session is not valid for this storage
     */
    protected SessionCommon validateSession ( Session session ) throws InvalidSessionException
    {
        if ( session == null )
            throw new InvalidSessionException ();
        
        synchronized ( session )
        {
            if ( ! ( session instanceof SessionCommon ) )
                throw new InvalidSessionException ();

            SessionCommon sessionCommon = (SessionCommon)session;
            if ( sessionCommon.getStorage () != this )
                throw new InvalidSessionException ();

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

    synchronized public QueryDescription[] getQueries ( Session session ) throws InvalidSessionException
    {
        @SuppressWarnings("unused")
        SessionCommon sessionCommon = validateSession ( session );
        
        List<QueryDescription> descriptions = new ArrayList<QueryDescription> ( _queries.size () );
        for ( QueryEntry entry : _queries.values () )
        {
            descriptions.add ( new QueryDescription ( entry.getDescription () ) );
        }
        
        return descriptions.toArray ( new QueryDescription[descriptions.size ()] );
    }

    synchronized public Event[] read ( Session session, String queryID ) throws InvalidSessionException, NoSuchQueryException
    {
        @SuppressWarnings("unused")
        SessionCommon sessionCommon = validateSession ( session );
        
        QueryEntry queryEntry = _queries.get ( queryID );
        
        if ( queryEntry == null )
            throw new NoSuchQueryException ();
        
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
    
    public void submitEvent ( Properties properties, Event event ) throws Exception
    {   
        validateSubmission ( properties, event );
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
}
