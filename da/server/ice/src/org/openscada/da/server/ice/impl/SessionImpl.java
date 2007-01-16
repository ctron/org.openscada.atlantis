package org.openscada.da.server.ice.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.core.Variant;
import org.openscada.core.ice.AttributesHelper;
import org.openscada.core.ice.VariantHelper;
import org.openscada.da.core.server.ItemChangeListener;
import org.openscada.da.core.server.Session;

import Ice.Current;
import OpenSCADA.DA.DataCallbackPrx;
import OpenSCADA.DA._SessionDisp;

public class SessionImpl extends _SessionDisp implements ItemChangeListener
{
    private Session _session;
    private DataCallbackPrx _callback = null;
    
    public SessionImpl ( Session session )
    {
        super ();
        _session = session;
        _session.setListener ( this );
    }
    
    public synchronized void registerCallback ( DataCallbackPrx dataCallback, Current __current )
    {
        _callback = dataCallback;
    }
    
    public synchronized void unregisterCallback ( Current __current )
    {
        _callback = null;
    }

    public Session getSession ()
    {
        return _session;
    }

    public void attributesChanged ( String name, Map<String, Variant> attributes, boolean initial )
    {
        if ( _callback == null )
            return;
        
        AsyncAttributesChange cb = new AsyncAttributesChange ( this );
        
        _callback.attributesChange_async ( cb, name, AttributesHelper.toIce ( attributes ), initial );
    }

    public synchronized void valueChanged ( String name, Variant value, boolean initial )
    {
        if ( _callback == null )
            return;
        
        AsyncValueChange cb = new AsyncValueChange ( this );
        
        _callback.valueChange_async ( cb, name, VariantHelper.toIce ( value ), initial );
    }

    public void handleListenerError ()
    {
        // FIXME: destroy session
    }
}
