/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.ice.impl;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.core.ice.AttributesHelper;
import org.openscada.core.ice.VariantHelper;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.ItemChangeListener;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.browser.FolderListener;
import org.openscada.da.ice.BrowserEntryHelper;

import Ice.Current;
import Ice.Identity;
import OpenSCADA.Core.InvalidSessionException;
import OpenSCADA.DA.DataCallbackPrx;
import OpenSCADA.DA.DataCallbackPrxHelper;
import OpenSCADA.DA._SessionDisp;
import OpenSCADA.DA.Browser.FolderCallbackPrx;
import OpenSCADA.DA.Browser.FolderCallbackPrxHelper;

public class SessionImpl extends _SessionDisp implements ItemChangeListener, FolderListener
{
    private static Logger _log = Logger.getLogger ( SessionImpl.class );

    private HiveImpl _hive;

    private Session _session;

    private DataCallbackPrx _dataCallback = null;

    private FolderCallbackPrx _folderCallback = null;

    public SessionImpl ( final HiveImpl hive, final Session session )
    {
        super ();
        this._hive = hive;
        this._session = session;
        this._session.setListener ( (ItemChangeListener)this );
        this._session.setListener ( (FolderListener)this );

        System.gc ();
    }

    @Override
    protected void finalize () throws Throwable
    {
        _log.debug ( "Session finalized" );
        super.finalize ();
    }

    public void setDataCallback ( final Identity ident, final Current __current )
    {
        this._dataCallback = DataCallbackPrxHelper.uncheckedCast ( __current.con.createProxy ( ident ).ice_oneway () );
    }

    public void unsetDataCallback ( final Current __current )
    {
        this._dataCallback = null;
    }

    public void setFolderCallback ( final Identity ident, final Current __current )
    {
        this._folderCallback = FolderCallbackPrxHelper.uncheckedCast ( __current.con.createProxy ( ident ).ice_oneway () );
    }

    public void unsetFolderCallback ( final Current __current )
    {
        this._folderCallback = null;
    }

    public Session getSession ()
    {
        return this._session;
    }

    public void dataChanged ( final String itemId, final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        _log.debug ( String.format ( "Data changed for '%s'", itemId ) );

        DataCallbackPrx dataCallback;

        if ( ( dataCallback = this._dataCallback ) != null )
        {
            dataCallback.dataChange ( itemId, VariantHelper.toIce ( value ), AttributesHelper.toIce ( attributes ), cache );
        }
    }

    public void handleListenerError ()
    {
        _log.info ( "handleListenerError" );
        destroy ();
    }

    public synchronized void destroy ()
    {
        _log.debug ( "destroy session" );

        if ( this._session == null )
        {
            return;
        }

        this._dataCallback = null;
        this._folderCallback = null;
        this._session.setListener ( (ItemChangeListener)null );
        this._session.setListener ( (FolderListener)null );

        try
        {
            this._hive.closeSession ( this );
        }
        catch ( final InvalidSessionException e )
        {
            // we don't care
        }

        this._session = null;
        this._hive = null;
    }

    public void folderChanged ( final Location location, final Collection<Entry> added, final Collection<String> removed, final boolean full )
    {
        _log.debug ( String.format ( "Folder changed: %s", location.toString () ) );

        if ( this._folderCallback == null )
        {
            _log.debug ( "Folder changed but no listener subscribed" );
            return;
        }

        FolderCallbackPrx folderCallback;

        if ( ( folderCallback = this._folderCallback ) != null )
        {
            folderCallback.folderChanged ( location.asArray (), BrowserEntryHelper.toIce ( added.toArray ( new Entry[0] ) ), removed.toArray ( new String[0] ), full );
        }
    }

    public void ping ()
    {
        try
        {
            DataCallbackPrx dataCallback = this._dataCallback;
            if ( dataCallback != null )
            {
                dataCallback = DataCallbackPrxHelper.uncheckedCast ( dataCallback.ice_twoway () );
                dataCallback.ice_ping ();
            }

            FolderCallbackPrx folderCallback = this._folderCallback;
            if ( folderCallback != null )
            {
                folderCallback = FolderCallbackPrxHelper.uncheckedCast ( folderCallback.ice_twoway () );
                folderCallback.ice_ping ();
            }
        }
        catch ( final Throwable e )
        {
            _log.debug ( "Ping failed", e );
            handleListenerError ();
        }
    }

    public void subscriptionChanged ( final String item, final SubscriptionState subscriptionState )
    {
        _log.debug ( String.format ( "Subscription changed: '%s' - '%s'", item, subscriptionState.name () ) );

        DataCallbackPrx dataCallback;

        if ( ( dataCallback = this._dataCallback ) != null )
        {
            OpenSCADA.DA.SubscriptionState ss = OpenSCADA.DA.SubscriptionState.DISCONNECTED;

            switch ( subscriptionState )
            {
            case CONNECTED:
                ss = OpenSCADA.DA.SubscriptionState.CONNECTED;
                break;
            case DISCONNECTED:
                ss = OpenSCADA.DA.SubscriptionState.DISCONNECTED;
                break;
            case GRANTED:
                ss = OpenSCADA.DA.SubscriptionState.GRANTED;
                break;
            }

            dataCallback.subscriptionChange ( item, ss );
        }
    }
}
