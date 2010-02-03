/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.ice.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.Variant;
import org.openscada.core.ice.AttributesHelper;
import org.openscada.core.ice.VariantHelper;
import org.openscada.da.core.Location;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.core.server.Hive;
import org.openscada.da.core.server.InvalidItemException;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.browser.HiveBrowser;
import org.openscada.da.core.server.browser.NoSuchFolderException;
import org.openscada.da.ice.BrowserEntryHelper;
import org.openscada.utils.concurrent.NotifyFuture;

import Ice.Current;
import Ice.ObjectAdapter;
import OpenSCADA.Core.OperationNotSupportedException;
import OpenSCADA.Core.VariantBase;
import OpenSCADA.DA.AMD_Hive_write;
import OpenSCADA.DA.AMD_Hive_writeAttributes;
import OpenSCADA.DA.SessionPrx;
import OpenSCADA.DA.SessionPrxHelper;
import OpenSCADA.DA.WriteAttributesResultEntry;
import OpenSCADA.DA._HiveDisp;
import OpenSCADA.DA.Browser.Entry;
import OpenSCADA.DA.Browser.InvalidLocationException;

public class HiveImpl extends _HiveDisp implements Runnable
{
    private static Logger logger = Logger.getLogger ( HiveImpl.class );

    private Hive hive = null;

    private final Map<SessionPrx, SessionImpl> sessionMap = new HashMap<SessionPrx, SessionImpl> ();

    private final Map<SessionImpl, SessionPrx> sessionMapRev = new HashMap<SessionImpl, SessionPrx> ();

    private Thread pingThread = null;

    private ObjectAdapter adapter = null;

    private volatile boolean running = true;

    public HiveImpl ( final Hive hive, final ObjectAdapter adapter )
    {
        super ();
        this.hive = hive;
        this.adapter = adapter;

        start ();
    }

    public void start ()
    {
        this.running = true;
        this.pingThread = new Thread ( this );
        this.pingThread.setDaemon ( true );
        this.pingThread.start ();
    }

    public void stop ()
    {
        this.running = false;
    }

    public void run ()
    {
        while ( this.running )
        {
            try
            {
                Thread.sleep ( 1000 );
            }
            catch ( final InterruptedException e )
            {
            }

            synchronized ( this )
            {
                for ( final SessionImpl session : this.sessionMapRev.keySet () )
                {
                    session.ping ();
                }
            }
        }
    }

    @SuppressWarnings ( "unchecked" )
    public SessionPrx createSession ( final Map properties, final Current __current ) throws OpenSCADA.DA.UnableToCreateSession
    {
        final Properties props = new Properties ();
        for ( final Object o : properties.entrySet () )
        {
            final Map.Entry entry = (Map.Entry)o;
            props.put ( entry.getKey ().toString (), entry.getValue ().toString () );
        }

        try
        {
            final SessionImpl session = new SessionImpl ( this, (Session)this.hive.createSession ( props ) );
            final SessionPrx sessionProxy = SessionPrxHelper.uncheckedCast ( __current.adapter.addWithUUID ( session ) );
            this.sessionMap.put ( sessionProxy, session );
            this.sessionMapRev.put ( session, sessionProxy );
            return sessionProxy;
        }
        catch ( final UnableToCreateSessionException e )
        {
            throw new OpenSCADA.DA.UnableToCreateSession ();
        }
    }

    public void subscribeItem ( final OpenSCADA.DA.SessionPrx session, final String item, final Current __current ) throws OpenSCADA.Core.InvalidSessionException, OpenSCADA.DA.InvalidItemException
    {
        logger.debug ( String.format ( "Register for item '%s'", item ) );

        final SessionImpl sessionImpl = getSession ( session );
        try
        {
            this.hive.subscribeItem ( sessionImpl.getSession (), item );
        }
        catch ( final InvalidSessionException e )
        {
            throw new OpenSCADA.Core.InvalidSessionException ();
        }
        catch ( final InvalidItemException e )
        {
            throw new OpenSCADA.DA.InvalidItemException ( item );
        }
    }

    public void unsubscribeItem ( final OpenSCADA.DA.SessionPrx session, final String item, final Current __current ) throws OpenSCADA.Core.InvalidSessionException, OpenSCADA.DA.InvalidItemException
    {
        logger.debug ( String.format ( "Un-Register for item '%s'", item ) );

        final SessionImpl sessionImpl = getSession ( session );
        try
        {
            this.hive.unsubscribeItem ( sessionImpl.getSession (), item );
        }
        catch ( final InvalidSessionException e )
        {
            throw new OpenSCADA.Core.InvalidSessionException ();
        }
        catch ( final InvalidItemException e )
        {
            throw new OpenSCADA.DA.InvalidItemException ( item );
        }
    }

    public void write_async ( final AMD_Hive_write __cb, final OpenSCADA.DA.SessionPrx session, final String item, final VariantBase value, final Current __current ) throws OpenSCADA.Core.InvalidSessionException, OpenSCADA.DA.InvalidItemException
    {
        logger.debug ( String.format ( "write request: item - '%s'", item ) );

        final SessionImpl sessionImpl = getSession ( session );

        final Variant variant = VariantHelper.fromIce ( value );

        final AMD_Hive_write cb = __cb;

        try
        {
            final WriteResult result = this.hive.startWrite ( sessionImpl.getSession (), item, variant ).get ();

            if ( result.isSuccess () )
            {
                cb.ice_response ();
            }
            else
            {
                cb.ice_exception ( new InvocationTargetException ( result.getError () ) );
            }

        }
        catch ( final InvalidSessionException e )
        {
            throw new OpenSCADA.Core.InvalidSessionException ();
        }
        catch ( final InvalidItemException e )
        {
            throw new OpenSCADA.DA.InvalidItemException ();
        }
        catch ( final Throwable e )
        {
            cb.ice_exception ( new InvocationTargetException ( e ) );
        }
    }

    @SuppressWarnings ( "unchecked" )
    public void writeAttributes_async ( final AMD_Hive_writeAttributes __cb, final OpenSCADA.DA.SessionPrx session, final String item, final Map attributes, final Current __current ) throws OpenSCADA.Core.InvalidSessionException, OpenSCADA.DA.InvalidItemException
    {
        logger.debug ( String.format ( "write attribute request: item - '%s'", item ) );

        final SessionImpl sessionImpl = getSession ( session );

        final Map<String, Variant> attr = AttributesHelper.fromIce ( attributes );

        final AMD_Hive_writeAttributes cb = __cb;

        try
        {
            final NotifyFuture<WriteAttributeResults> task = this.hive.startWriteAttributes ( sessionImpl.getSession (), item, attr );

            final WriteAttributeResults writeAttributeResults = task.get ();

            final List<WriteAttributesResultEntry> result = new LinkedList<WriteAttributesResultEntry> ();
            for ( final Map.Entry<String, WriteAttributeResult> entry : writeAttributeResults.entrySet () )
            {
                if ( entry.getValue ().isError () )
                {
                    logger.debug ( String.format ( "Failed to write attribute '%s': '%s'", entry.getKey (), entry.getValue ().getError ().getMessage () ) );
                    result.add ( new WriteAttributesResultEntry ( entry.getKey (), entry.getValue ().getError ().getMessage () ) );
                }
                else
                {
                    result.add ( new WriteAttributesResultEntry ( entry.getKey (), null ) );
                }
            }
            cb.ice_response ( result.toArray ( new WriteAttributesResultEntry[0] ) );

        }
        catch ( final InvalidSessionException e )
        {
            throw new OpenSCADA.Core.InvalidSessionException ();
        }
        catch ( final InvalidItemException e )
        {
            throw new OpenSCADA.DA.InvalidItemException ();
        }
        catch ( final Exception e )
        {
            cb.ice_exception ( e );
        }

    }

    protected SessionImpl getSession ( final OpenSCADA.Core.SessionPrx session ) throws OpenSCADA.Core.InvalidSessionException
    {
        final SessionImpl sessionImpl = this.sessionMap.get ( session );

        if ( sessionImpl == null )
        {
            throw new OpenSCADA.Core.InvalidSessionException ();
        }

        return sessionImpl;
    }

    public void closeSession ( final OpenSCADA.Core.SessionPrx session, final Current __current ) throws OpenSCADA.Core.InvalidSessionException
    {
        final SessionImpl sessionImpl = getSession ( session );

        closeSession ( sessionImpl );
    }

    public Entry[] browse ( final SessionPrx session, final String[] location, final Current __current ) throws OpenSCADA.Core.InvalidSessionException, InvalidLocationException, OperationNotSupportedException
    {
        final SessionImpl sessionImpl = getSession ( session );

        final HiveBrowser browser = this.hive.getBrowser ();
        if ( browser == null )
        {
            throw new OpenSCADA.Core.OperationNotSupportedException ( "The hive has no root folder registered" );
        }

        try
        {
            final NotifyFuture<org.openscada.da.core.browser.Entry[]> task = browser.startBrowse ( sessionImpl.getSession (), new Location ( location ) );
            return BrowserEntryHelper.toIce ( task.get () );
        }
        catch ( final ExecutionException e )
        {
            if ( e.getCause () instanceof NoSuchFolderException )
            {
                throw new OpenSCADA.DA.Browser.InvalidLocationException ();
            }
            else
            {
                throw new OpenSCADA.Core.OperationNotSupportedException ( e.getMessage () );
            }
        }
        catch ( final InvalidSessionException e )
        {
            throw new OpenSCADA.Core.InvalidSessionException ();
        }
        catch ( final InterruptedException e )
        {
            throw new OpenSCADA.Core.OperationNotSupportedException ( e.getMessage () );
        }
        finally
        {

        }
    }

    public void subscribeFolder ( final SessionPrx session, final String[] location, final Current __current ) throws OpenSCADA.Core.InvalidSessionException, InvalidLocationException, OperationNotSupportedException
    {
        final SessionImpl sessionImpl = getSession ( session );

        final HiveBrowser browser = this.hive.getBrowser ();
        if ( browser == null )
        {
            throw new OpenSCADA.Core.OperationNotSupportedException ();
        }

        try
        {
            browser.subscribe ( sessionImpl.getSession (), new Location ( location ) );
        }
        catch ( final NoSuchFolderException e )
        {
            throw new OpenSCADA.DA.Browser.InvalidLocationException ();
        }
        catch ( final InvalidSessionException e )
        {
            throw new OpenSCADA.Core.OperationNotSupportedException ();
        }
    }

    public void unsubscribeFolder ( final SessionPrx session, final String[] location, final Current __current ) throws OpenSCADA.Core.InvalidSessionException, InvalidLocationException, OperationNotSupportedException
    {
        final SessionImpl sessionImpl = getSession ( session );

        final HiveBrowser browser = this.hive.getBrowser ();
        if ( browser == null )
        {
            throw new OpenSCADA.Core.OperationNotSupportedException ();
        }

        try
        {
            browser.unsubscribe ( sessionImpl.getSession (), new Location ( location ) );
        }
        catch ( final NoSuchFolderException e )
        {
            throw new OpenSCADA.DA.Browser.InvalidLocationException ();
        }
        catch ( final InvalidSessionException e )
        {
            throw new OpenSCADA.Core.OperationNotSupportedException ();
        }
    }

    public void closeSession ( final SessionImpl session ) throws OpenSCADA.Core.InvalidSessionException
    {
        logger.debug ( "Close session" );

        try
        {
            final SessionPrx sessionProxy = this.sessionMapRev.get ( session );
            if ( sessionProxy == null )
            {
                logger.debug ( "No session proxy found" );
                return;
            }

            // remove the session object from ice
            this.adapter.remove ( sessionProxy.ice_getIdentity () );

            this.sessionMap.remove ( sessionProxy );
            this.sessionMapRev.remove ( session );

            this.hive.closeSession ( session.getSession () );

            session.destroy ();
        }
        catch ( final InvalidSessionException e )
        {
            throw new OpenSCADA.Core.InvalidSessionException ();
        }
    }

}
