package org.openscada.da.server.ice.impl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.core.ice.AttributesHelper;
import org.openscada.core.ice.VariantHelper;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.ItemChangeListener;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.browser.FolderListener;
import org.openscada.da.ice.BrowserEntryHelper;

import Ice.Current;
import Ice.Identity;
import OpenSCADA.DA.DataCallbackPrx;
import OpenSCADA.DA.DataCallbackPrxHelper;
import OpenSCADA.DA._SessionDisp;
import OpenSCADA.DA.Browser.FolderCallbackPrx;
import OpenSCADA.DA.Browser.FolderCallbackPrxHelper;

public class SessionImpl extends _SessionDisp implements ItemChangeListener, FolderListener
{
    private static Logger _log = Logger.getLogger ( SessionImpl.class );
    
    private Session _session;
    private DataCallbackPrx _dataCallback = null;
    private FolderCallbackPrx _folderCallback = null;
    
    public SessionImpl ( Session session )
    {
        super ();
        _session = session;
        _session.setListener ( (ItemChangeListener)this );
        _session.setListener ( (FolderListener)this );
    }
    
    public synchronized void setDataCallback ( Identity ident, Current __current )
    {
        _dataCallback = DataCallbackPrxHelper.uncheckedCast ( __current.con.createProxy ( ident ) );
    }
    
    public synchronized void unsetDataCallback ( Current __current )
    {
        _dataCallback = null;
    }
    
    public void setFolderCallback ( Identity ident, Current __current )
    {
        _folderCallback = FolderCallbackPrxHelper.uncheckedCast ( __current.con.createProxy ( ident ) );
    }

    public void unsetFolderCallback ( Current __current )
    {
        _folderCallback = null;
    }

    public Session getSession ()
    {
        return _session;
    }

    public synchronized void attributesChanged ( String name, Map<String, Variant> attributes, boolean initial )
    {
        _log.debug ( String.format ( "Attributes changed for '%s'", name ) );
        
        if ( _dataCallback == null )
            return;
        
        AsyncAttributesChange cb = new AsyncAttributesChange ( this );
        
        _dataCallback.attributesChange_async ( cb, name, AttributesHelper.toIce ( attributes ), initial );
    }

    public synchronized void valueChanged ( String name, Variant value, boolean initial )
    {
        _log.debug ( String.format ( "Value changed for '%s'", name ) );
        
        if ( _dataCallback == null )
            return;
        
        AsyncValueChange cb = new AsyncValueChange ( this );
        
        _dataCallback.valueChange_async ( cb, name, VariantHelper.toIce ( value ), initial );
    }

    public synchronized void handleListenerError ()
    {
        _dataCallback = null;
        _session.setListener ( (ItemChangeListener)null );
    }

    public synchronized void folderChanged ( Location location, Collection<Entry> added, Collection<String> removed, boolean full )
    {
       _log.debug ( String.format ( "Folder changed: %s", location.toString () ) );
       
       if ( _folderCallback == null )
       {
           _log.debug ( "Folder changed but no listener subscribed" );
           return;
       }
       
       AsyncFolderChange cb = new AsyncFolderChange ( this );
       _folderCallback.folderChanged_async ( cb, location.asArray (), BrowserEntryHelper.toIce ( added.toArray ( new Entry[0] ) ), removed.toArray ( new String[0] ), full );
    }
}
