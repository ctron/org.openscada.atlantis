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
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.server.Hive;
import org.openscada.da.core.server.InvalidItemException;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.WriteAttributesOperationListener;
import org.openscada.da.core.server.WriteOperationListener;

import Ice.Current;
import OpenSCADA.Core.VariantBase;
import OpenSCADA.DA.AMD_Hive_write;
import OpenSCADA.DA.AMD_Hive_writeAttributes;
import OpenSCADA.DA.SessionPrx;
import OpenSCADA.DA.SessionPrxHelper;
import OpenSCADA.DA._HiveDisp;

public class HiveImpl extends _HiveDisp
{
    private static Logger _log = Logger.getLogger ( HiveImpl.class );
    
    private Hive _hive = null;
    
    private Map<SessionPrx,SessionImpl> _sessionMap = new HashMap<SessionPrx,SessionImpl> ();
    
    public HiveImpl ( Hive hive )
    {
        super ();
        _hive = hive;
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
            SessionImpl session = new SessionImpl ( _hive.createSession ( props ) );
            SessionPrx sessionProxy = SessionPrxHelper.uncheckedCast ( __current.adapter.addWithUUID ( session ) );
            _sessionMap.put ( sessionProxy, session );
            return sessionProxy;
        }
        catch ( UnableToCreateSessionException e )
        {
            throw new OpenSCADA.DA.UnableToCreateSession ();
        }
    }

    public void registerForItem ( OpenSCADA.Core.SessionPrx session, String item, Current __current )
    {
        // TODO Auto-generated method stub

    }

    public void unregisterForItem ( OpenSCADA.Core.SessionPrx session, String item, Current __current )
    {
        // TODO Auto-generated method stub

    }

    public void write_async ( AMD_Hive_write __cb, OpenSCADA.Core.SessionPrx session, String item, VariantBase value, Current __current ) throws OpenSCADA.Core.InvalidSessionException, OpenSCADA.DA.InvalidItemException
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

    public void writeAttributes_async ( AMD_Hive_writeAttributes __cb, OpenSCADA.Core.SessionPrx session, String item, Map attributes, Current __current ) throws OpenSCADA.Core.InvalidSessionException, OpenSCADA.DA.InvalidItemException
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
        
        try
        {
            _sessionMap.remove ( session );
            _hive.closeSession ( sessionImpl.getSession () );
        }
        catch ( InvalidSessionException e )
        {
            throw new OpenSCADA.Core.InvalidSessionException ();
        }
    }
}
