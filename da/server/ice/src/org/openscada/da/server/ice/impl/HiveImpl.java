/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.Variant;
import org.openscada.core.ice.AttributesHelper;
import org.openscada.core.ice.VariantHelper;
import org.openscada.da.core.Location;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.server.Hive;
import org.openscada.da.core.server.InvalidItemException;
import org.openscada.da.core.server.WriteAttributesOperationListener;
import org.openscada.da.core.server.WriteOperationListener;
import org.openscada.da.core.server.browser.HiveBrowser;
import org.openscada.da.core.server.browser.NoSuchFolderException;
import org.openscada.da.ice.BrowserEntryHelper;

import Ice.Current;
import Ice.ObjectAdapter;
import OpenSCADA.Core.OperationNotSupportedException;
import OpenSCADA.Core.VariantBase;
import OpenSCADA.DA.AMD_Hive_write;
import OpenSCADA.DA.AMD_Hive_writeAttributes;
import OpenSCADA.DA.SessionPrx;
import OpenSCADA.DA.SessionPrxHelper;
import OpenSCADA.DA._HiveDisp;
import OpenSCADA.DA.Browser.Entry;
import OpenSCADA.DA.Browser.InvalidLocationException;

public class HiveImpl extends _HiveDisp implements Runnable
{
    private static Logger _log = Logger.getLogger ( HiveImpl.class );
    
    private Hive _hive = null;
    
    private Map<SessionPrx, SessionImpl> _sessionMap = new HashMap<SessionPrx,SessionImpl> ();
    private Map<SessionImpl, SessionPrx> _sessionMapRev = new HashMap<SessionImpl, SessionPrx> ();
    
    private Thread _pingThread = null;
    private ObjectAdapter _adapter = null;
    
    public HiveImpl ( Hive hive, ObjectAdapter adapter )
    {
        super ();
        _hive = hive;
        _adapter = adapter;
        
        _pingThread = new Thread ( this );
        _pingThread.setDaemon ( true );
        _pingThread.start ();
    }
    
    public void run ()
    {
        while ( true )
        {
            try
            {
                Thread.sleep ( 1000 );
            }
            catch ( InterruptedException e )
            {
            }
            
            synchronized ( this )
            {
                for ( SessionImpl session : _sessionMapRev.keySet () )
                {
                    session.ping ();
                }
            }
        }
    }

    public SessionPrx createSession ( Map properties, Current __current ) throws OpenSCADA.DA.UnableToCreateSession
    {
        Properties props = new Properties ();
        for ( Object o : properties.entrySet () )
        {
            Map.Entry entry = (Map.Entry)o;
            props.put ( entry.getKey ().toString (), entry.getValue ().toString () );
        }
        
        try
        {
            SessionImpl session = new SessionImpl ( this, _hive.createSession ( props ) );
            SessionPrx sessionProxy = SessionPrxHelper.uncheckedCast ( __current.adapter.addWithUUID ( session ) );
            _sessionMap.put ( sessionProxy, session );
            _sessionMapRev.put ( session, sessionProxy );
            return sessionProxy;
        }
        catch ( UnableToCreateSessionException e )
        {
            throw new OpenSCADA.DA.UnableToCreateSession ();
        }
    }

    public void registerForItem ( OpenSCADA.DA.SessionPrx session, String item, boolean initialCacheRead, Current __current ) throws OpenSCADA.Core.InvalidSessionException, OpenSCADA.DA.InvalidItemException
    {
        _log.debug ( String.format ( "Register for item '%s'", item ) );
        
        SessionImpl sessionImpl = getSession ( session );
        try
        {
            _hive.registerForItem ( sessionImpl.getSession (), item, initialCacheRead );
        }
        catch ( InvalidSessionException e )
        {
            throw new OpenSCADA.Core.InvalidSessionException ();
        }
        catch ( InvalidItemException e )
        {
            throw new OpenSCADA.DA.InvalidItemException ( item );
        }
    }

    public void unregisterForItem ( OpenSCADA.DA.SessionPrx session, String item, Current __current ) throws OpenSCADA.Core.InvalidSessionException, OpenSCADA.DA.InvalidItemException
    {
        _log.debug ( String.format ( "Un-Register for item '%s'", item ) );
        
        SessionImpl sessionImpl = getSession ( session );
        try
        {
            _hive.unregisterForItem ( sessionImpl.getSession (), item );
        }
        catch ( InvalidSessionException e )
        {
            throw new OpenSCADA.Core.InvalidSessionException ();
        }
        catch ( InvalidItemException e )
        {
            throw new OpenSCADA.DA.InvalidItemException ( item );
        }
    }

    public void write_async ( AMD_Hive_write __cb, OpenSCADA.DA.SessionPrx session, String item, VariantBase value, Current __current ) throws OpenSCADA.Core.InvalidSessionException, OpenSCADA.DA.InvalidItemException
    {
        _log.debug ( String.format ( "write request: item - '%s'", item ) );
        
        SessionImpl sessionImpl = getSession ( session );
        
        Variant variant = VariantHelper.fromIce ( value );
        
        final AMD_Hive_write cb = __cb; 
        
        try
        {
            long id = _hive.startWrite ( sessionImpl.getSession (), item, variant, new WriteOperationListener () {

                public void failure ( Throwable throwable )
                {
                    cb.ice_exception ( new Exception ( throwable ) );
                }

                public void success ()
                {
                    cb.ice_response ();
                }} );
            _hive.thawOperation ( sessionImpl.getSession (), id );
        }
        catch ( InvalidSessionException e )
        {
            throw new OpenSCADA.Core.InvalidSessionException ();
        }
        catch ( InvalidItemException e )
        {
            throw new OpenSCADA.DA.InvalidItemException ();
        }
    }

    public void writeAttributes_async ( AMD_Hive_writeAttributes __cb, OpenSCADA.DA.SessionPrx session, String item, Map attributes, Current __current ) throws OpenSCADA.Core.InvalidSessionException, OpenSCADA.DA.InvalidItemException
    {
        _log.debug ( String.format ( "write attribute request: item - '%s'", item ) );
        
        SessionImpl sessionImpl = getSession ( session );
        
        Map<String, Variant> attr = AttributesHelper.fromIce ( attributes );
        
        final AMD_Hive_writeAttributes cb = __cb; 
        
        try
        {
            long id = _hive.startWriteAttributes ( sessionImpl.getSession (), item, attr, new WriteAttributesOperationListener () {

                public void complete ( WriteAttributeResults writeAttributeResults )
                {
                    Map<String, String> result = new HashMap<String, String> ();
                    for ( Map.Entry<String, WriteAttributeResult> entry : writeAttributeResults.entrySet () )
                    {
                        if ( entry.getValue ().isError () )
                            result.put ( entry.getKey (), entry.getValue ().getError ().getMessage () );
                        else
                            result.put ( entry.getKey (), null );
                    }
                    cb.ice_response ( result );
                }

                public void failed ( Throwable error )
                {
                    cb.ice_exception ( new Exception ( error ) );
                }} );
            _hive.thawOperation ( sessionImpl.getSession (), id );
        }
        catch ( InvalidSessionException e )
        {
            throw new OpenSCADA.Core.InvalidSessionException ();
        }
        catch ( InvalidItemException e )
        {
            throw new OpenSCADA.DA.InvalidItemException ();
        }
    }
    
    protected SessionImpl getSession ( OpenSCADA.Core.SessionPrx session ) throws OpenSCADA.Core.InvalidSessionException
    {
        SessionImpl sessionImpl = _sessionMap.get ( session );
        
        if ( sessionImpl == null )
            throw new OpenSCADA.Core.InvalidSessionException ();
        
        return sessionImpl;
    }

    public void closeSession ( OpenSCADA.Core.SessionPrx session, Current __current ) throws OpenSCADA.Core.InvalidSessionException
    {
        SessionImpl sessionImpl = getSession ( session );
        
        closeSession ( sessionImpl );
    }

    public Entry[] browse ( SessionPrx session, String[] location, Current __current ) throws OpenSCADA.Core.InvalidSessionException, InvalidLocationException, OperationNotSupportedException
    {
        SessionImpl sessionImpl = getSession ( session );
        
        HiveBrowser browser = _hive.getBrowser ();
        if ( browser == null )
            throw new OpenSCADA.Core.OperationNotSupportedException ();
        
        try
        {
            org.openscada.da.core.browser.Entry [] entries = browser.list ( sessionImpl.getSession (), new Location ( location ) );
            return BrowserEntryHelper.toIce ( entries );
        }
        catch ( InvalidSessionException e )
        {
            throw new OpenSCADA.Core.OperationNotSupportedException ();
        }
        catch ( NoSuchFolderException e )
        {
            throw new OpenSCADA.DA.Browser.InvalidLocationException ();
        }
    }

    public void subscribeFolder ( SessionPrx session, String[] location, Current __current ) throws OpenSCADA.Core.InvalidSessionException, InvalidLocationException, OperationNotSupportedException
    {
        SessionImpl sessionImpl = getSession ( session );
        
        HiveBrowser browser = _hive.getBrowser ();
        if ( browser == null )
            throw new OpenSCADA.Core.OperationNotSupportedException ();
        
        try
        {
            browser.subscribe ( sessionImpl.getSession (), new Location ( location ) );
        }
        catch ( NoSuchFolderException e )
        {
            throw new OpenSCADA.DA.Browser.InvalidLocationException ();
        }
        catch ( InvalidSessionException e )
        {
            throw new OpenSCADA.Core.OperationNotSupportedException ();
        }
    }

    public void unsubscribeFolder ( SessionPrx session, String[] location, Current __current ) throws OpenSCADA.Core.InvalidSessionException, InvalidLocationException, OperationNotSupportedException
    {
        SessionImpl sessionImpl = getSession ( session );
        
        HiveBrowser browser = _hive.getBrowser ();
        if ( browser == null )
            throw new OpenSCADA.Core.OperationNotSupportedException ();
        
        try
        {
            browser.unsubscribe ( sessionImpl.getSession (), new Location ( location ) );
        }
        catch ( NoSuchFolderException e )
        {
            throw new OpenSCADA.DA.Browser.InvalidLocationException ();
        }
        catch ( InvalidSessionException e )
        {
            throw new OpenSCADA.Core.OperationNotSupportedException ();
        }
    }

    public void closeSession ( SessionImpl session ) throws OpenSCADA.Core.InvalidSessionException
    {
        _log.debug ( "Close session" );
        
        try
        {
            SessionPrx sessionProxy = _sessionMapRev.get ( session );
            if ( sessionProxy == null )
                return;
            
            // remove the session object from ice
            _adapter.remove ( sessionProxy.ice_getIdentity () );
            
            _sessionMap.remove ( sessionProxy );
            _sessionMapRev.remove ( session );
            
            session.destroy ();
            _hive.closeSession ( session.getSession () );
        }
        catch ( InvalidSessionException e )
        {
            throw new OpenSCADA.Core.InvalidSessionException ();
        }   
    }


}
