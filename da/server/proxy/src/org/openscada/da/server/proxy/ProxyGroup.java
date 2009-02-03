/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.proxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.Connection;
import org.openscada.da.client.ItemManager;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.core.Location;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.AttributeMode;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class ProxyGroup
{
    private final List<ConnectionStateListener> connectionStateListeners = Collections.synchronizedList ( new ArrayList<ConnectionStateListener> () );

    private ProxySubConnectionId currentConnection;

    private ProxyPrefixName prefix;

    private final Map<String, ProxyDataItem> registeredItems = Collections.synchronizedMap ( new HashMap<String, ProxyDataItem> () );

    private FolderCommon connectionFolder;

    /**
     * 
     */
    public void start ()
    {
        createProxyFolder ();
    }

    /**
     * 
     */
    public void stop ()
    {
        destroyProxyFolder ();
    }

    /**
     * @return folder which holds items and connection information
     */
    public FolderCommon getConnectionFolder ()
    {
        return this.connectionFolder;
    }

    /**
     * @param connectionFolder
     */
    public void setConnectionFolder ( final FolderCommon connectionFolder )
    {
        this.connectionFolder = connectionFolder;
    }

    private final String separator;

    private final Map<ProxySubConnectionId, ProxySubConnection> subConnections = new HashMap<ProxySubConnectionId, ProxySubConnection> ();

    private Integer wait = 0;

    private ProxyFolder proxyFolder;

    /**
     * @param separator
     * @param prefix
     */
    public ProxyGroup ( final String separator, final ProxyPrefixName prefix )
    {
        this.separator = separator;
        this.prefix = prefix;
    }

    /**
     * @return
     */
    private Connection currentConnection ()
    {
        return currentSubConnection ().getConnection ();
    }

    /**
     * @return
     */
    private ProxySubConnection currentSubConnection ()
    {
        return this.subConnections.get ( this.currentConnection );
    }

    /**
     * @param connection
     * @param id
     * @param prefix
     * @throws InvalidOperationException
     * @throws NullValueException
     * @throws NotConvertableException
     */
    public void addConnection ( final Connection connection, final String id, final ProxyPrefixName prefix ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        final ProxySubConnectionId proxySubConnectionId = new ProxySubConnectionId ( id );
        if ( this.subConnections.containsKey ( proxySubConnectionId ) )
        {
            throw new IllegalArgumentException ( "connection with id " + proxySubConnectionId + " already exists!" );
        }
        final ProxySubConnection proxySubConnection = new ProxySubConnection ( connection, proxySubConnectionId, prefix );
        this.subConnections.put ( proxySubConnectionId, proxySubConnection );

        if ( this.currentConnection == null )
        {
            this.currentConnection = proxySubConnectionId;
        }
    }

    /**
     * @param connectionStateListener
     */
    public void addConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        this.connectionStateListeners.add ( connectionStateListener );
        currentConnection ().addConnectionStateListener ( connectionStateListener );
    }

    /**
     * @return name of currently active connection
     */
    public ProxySubConnectionId getCurrentConnection ()
    {
        return this.currentConnection;
    }

    /**
     * @return item prefix for this proxy, which will replace original prefix
     */
    public ProxyPrefixName getPrefix ()
    {
        return this.prefix;
    }

    /**
     * @return all available items which are already subscribed
     */
    public Map<String, ProxyDataItem> getRegisteredItems ()
    {
        return this.registeredItems;
    }

    /**
     * @return separator which separates prefix from rest of item name
     */
    public String getSeparator ()
    {
        return this.separator;
    }

    /**
     * @return map with all added subconnections
     */
    public Map<ProxySubConnectionId, ProxySubConnection> getSubConnections ()
    {
        return this.subConnections;
    }

    /**
     * @return time how long proxy should wait if subconnection is lost,
     * before item is set on error
     */
    public Integer getWait ()
    {
        return this.wait;
    }

    /**
     * @param itemId
     * @return original item id
     */
    public String convertToOriginalId ( final String itemId )
    {
        return ProxyUtils.originalItemId ( itemId, this.separator, getPrefix (), currentSubConnection ().getPrefix () );
    }

    /**
     * @param itemId
     * @return return name of item in proxy
     */
    public String convertToProxyId ( final String itemId )
    {
        return ProxyUtils.proxyItemId ( itemId, this.separator, getPrefix (), currentSubConnection ().getPrefix () );
    }

    /**
     * 
     */
    public void disconnectCurrentConnection ()
    {
        currentConnection ().disconnect ();
    }

    /**
     * 
     */
    public void connectCurrentConnection ()
    {
        currentConnection ().connect ();
    }

    /**
     * @param id
     * @return creates item and puts it in map
     */
    public ProxyDataItem realizeItem ( final String id )
    {
        ProxyDataItem item = this.registeredItems.get ( id );
        if ( item == null )
        {
            // create actual item
            final ProxyValueHolder pvh = new ProxyValueHolder ( this.separator, this.getPrefix (), this.getSubConnections (), this.getCurrentConnection () );
            item = new ProxyDataItem ( id, pvh );
            this.getRegisteredItems ().put ( id, item );

            setUpItem ( item, id );
        }
        return item;
    }

    private void setUpItem ( final ProxyDataItem item, final String requestId )
    {
        // hook up item
        for ( final ProxySubConnection subConnection : getSubConnections ().values () )
        {
            final ItemManager itemManager = subConnection.getItemManager ();
            final String originalItemId = ProxyUtils.originalItemId ( requestId, this.separator, getPrefix (), subConnection.getPrefix () );
            itemManager.addItemUpdateListener ( originalItemId, new ItemUpdateListener () {
                @Override
                public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
                {
                    item.getProxyValueHolder ().updateData ( subConnection.getId (), value, attributes, cache ? AttributeMode.SET : AttributeMode.UPDATE );
                }

                @Override
                public void notifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
                {
                    item.getProxyValueHolder ().updateSubscriptionState ( subConnection.getId (), subscriptionState, subscriptionError );
                }
            } );
        }
    }

    /**
     * @param connectionStateListener
     */
    public void removeConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        this.connectionStateListeners.remove ( connectionStateListener );
        currentConnection ().removeConnectionStateListener ( connectionStateListener );
    }

    /**
     * @param prefix
     */
    public void setPrefix ( final ProxyPrefixName prefix )
    {
        this.prefix = prefix;
    }

    /**
     * @param wait
     */
    public void setWait ( final Integer wait )
    {
        this.wait = wait;
    }

    /**
     * @param newConnectionId
     */
    public void switchTo ( final ProxySubConnectionId newConnectionId )
    {

        // remove 
        for ( final ConnectionStateListener listener : this.connectionStateListeners )
        {
            currentConnection ().removeConnectionStateListener ( listener );
        }

        for ( final ProxyDataItem proxyDataItem : this.registeredItems.values () )
        {
            proxyDataItem.getProxyValueHolder ().switchTo ( newConnectionId );
        }
        this.currentConnection = newConnectionId;
        for ( final ConnectionStateListener listener : this.connectionStateListeners )
        {
            currentConnection ().addConnectionStateListener ( listener );
        }

        createProxyFolder ();
    }

    private void destroyProxyFolder ()
    {
        // remove old folder
        if ( this.proxyFolder != null )
        {
            this.connectionFolder.remove ( this.proxyFolder );
            this.proxyFolder = null;
        }
    }

    private void createProxyFolder ()
    {
        // add new folder
        destroyProxyFolder ();

        this.proxyFolder = new ProxyFolder ( currentSubConnection ().getFolderManager (), this, new Location () );

        this.connectionFolder.add ( "items", this.proxyFolder, null );
    }

}
