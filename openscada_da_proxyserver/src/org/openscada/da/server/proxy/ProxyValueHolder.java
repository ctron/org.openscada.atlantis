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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.NoConnectionException;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.core.utils.AttributesHelper;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.server.common.AttributeMode;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class ProxyValueHolder
{
    private static Logger logger = Logger.getLogger ( ProxyValueHolder.class );

    private final Map<ProxySubConnectionId, DataItemValue> values = Collections.synchronizedMap ( new HashMap<ProxySubConnectionId, DataItemValue> () );

    private final Map<ProxySubConnectionId, ProxySubConnection> subConnections;

    private ProxySubConnectionId currentConnection;

    private ItemUpdateListener listener = null;

    private String separator = ".";

    private final ProxyPrefixName prefix;

    /**
     * This is the item Id of the proxy item
     */
    private final String proxyItemId;

    /**
     * @param currentConnection
     */
    public ProxyValueHolder ( final String separator, final ProxyPrefixName prefix, final Map<ProxySubConnectionId, ProxySubConnection> subConnections, final ProxySubConnectionId currentConnection, final String itemId )
    {
        this.separator = separator;
        this.prefix = prefix;
        this.subConnections = Collections.unmodifiableMap ( subConnections );
        this.currentConnection = currentConnection;
        this.proxyItemId = itemId;
        for ( final Entry<ProxySubConnectionId, ProxySubConnection> subConnectionEntry : subConnections.entrySet () )
        {
            this.values.put ( subConnectionEntry.getKey (), new DataItemValue () );
        }
    }

    /**
     * Switch between connections
     * @param newConnection
     */
    public void switchTo ( final ProxySubConnectionId newConnection )
    {
        synchronized ( this )
        {
            DataItemValue oldData = this.values.get ( this.currentConnection );
            DataItemValue newData = this.values.get ( newConnection );
            if ( oldData == null )
            {
                oldData = new DataItemValue ();
            }
            if ( newData == null )
            {
                newData = new DataItemValue ();
            }
            if ( !oldData.equals ( newData ) )
            {
                if ( newData.getValue () != null && !newData.getValue ().equals ( oldData.getValue () ) )
                {
                    this.listener.notifyDataChange ( newData.getValue (), newData.getAttributes (), true );
                }
                else if ( newData.getAttributes () != null && !newData.getAttributes ().equals ( oldData.getAttributes () ) )
                {
                    this.listener.notifyDataChange ( newData.getValue (), newData.getAttributes (), true );
                }
            }
            this.currentConnection = newConnection;
        }
    }

    /**
     * @param connection
     * @param value
     * @param attributes
     * @param mode
     */
    public void updateData ( final ProxySubConnectionId connection, final Variant value, final Map<String, Variant> attributes, final AttributeMode mode )
    {
        try
        {
            handleUpdateData ( connection, value, attributes, mode );
        }
        catch ( final Throwable e )
        {
            logger.error ( String.format ( "Failed to update data of item '%s'", this.proxyItemId ), e );
        }
    }

    /**
     * Wrap the event of data updates
     * @param connection the connection on which the even occurred
     * @param value the new value
     * @param attributes the new attributes
     * @param mode the attribute mode
     */
    private void handleUpdateData ( final ProxySubConnectionId connection, final Variant value, final Map<String, Variant> attributes, AttributeMode mode )
    {
        DataItemValue div;
        boolean doSend;

        synchronized ( this )
        {
            boolean changed = false;
            div = this.values.get ( connection );
            if ( value != null && !div.getValue ().equals ( value ) )
            {
                div.setValue ( new Variant ( value ) );
                changed = true;
            }
            if ( attributes != null )
            {
                if ( mode == null )
                {
                    mode = AttributeMode.UPDATE;
                }

                final Map<String, Variant> diff = new HashMap<String, Variant> ();
                if ( mode == AttributeMode.SET )
                {
                    AttributesHelper.set ( div.getAttributes (), attributes, diff );
                }
                else
                {
                    AttributesHelper.mergeAttributes ( div.getAttributes (), attributes, diff );
                }
                changed = changed || !diff.isEmpty ();
            }

            // make a copy
            div = new DataItemValue ( div );

            // check if we should send changes directly
            doSend = connection.equals ( this.currentConnection );
        }

        // now send outside of sync
        if ( doSend )
        {
            if ( sendOnLostConnection ( div ) )
            {
                this.listener.notifyDataChange ( value, attributes, false );
            }
        }
    }

    private boolean sendOnLostConnection ( final DataItemValue div )
    {
        return true;
        // return SubscriptionState.CONNECTED.equals ( div.getSubscriptionState () ) && ( ( div.getValue () != null ) && !getValue ().isNull () );
    }

    /**
     * @param itemId
     * @param value
     * @throws NoConnectionException
     * @throws OperationException
     */
    public void write ( final String itemId, final Variant value ) throws NoConnectionException, OperationException
    {
        final ProxySubConnection subConnection = this.subConnections.get ( this.currentConnection );
        final String actualItemId = ProxyUtils.originalItemId ( itemId, this.separator, this.prefix, subConnection.getPrefix () );
        subConnection.getConnection ().write ( actualItemId, value );
    }

    /**
     * @param itemId
     * @param attributes
     * @param writeAttributeResults 
     */
    public void writeAttributes ( final String itemId, final Map<String, Variant> attributes, final WriteAttributeResults writeAttributeResults )
    {
        final ProxySubConnection subConnection = this.subConnections.get ( this.currentConnection );
        final String actualItemId = ProxyUtils.originalItemId ( itemId, this.separator, this.prefix, subConnection.getPrefix () );
        WriteAttributeResults actualWriteAttributeResults;
        try
        {
            actualWriteAttributeResults = subConnection.getConnection ().writeAttributes ( actualItemId, attributes );
        }
        catch ( final NoConnectionException e )
        {
            actualWriteAttributeResults = attributesCouldNotBeWritten ( attributes, e );
        }
        catch ( final OperationException e )
        {
            actualWriteAttributeResults = attributesCouldNotBeWritten ( attributes, e );
        }
        writeAttributeResults.putAll ( actualWriteAttributeResults );
    }

    /**
     * creates a WriteAttributeResults object for given attributes filled 
     * with given exception for each attribute
     * @param attributes
     * @param e
     * @return
     */
    private WriteAttributeResults attributesCouldNotBeWritten ( final Map<String, Variant> attributes, final Exception e )
    {
        final WriteAttributeResults results = new WriteAttributeResults ();
        for ( final String name : attributes.keySet () )
        {
            results.put ( name, new WriteAttributeResult ( e ) );
        }
        return results;
    }

    /**
     * @return return current attribs
     */
    public Map<String, Variant> getAttributes ()
    {
        final DataItemValue div = this.values.get ( this.currentConnection );
        return div.getAttributes ();
    }

    /**
     * @return return current value
     */
    public Variant getValue ()
    {
        final DataItemValue div = this.values.get ( this.currentConnection );
        return div.getValue ();
    }

    /**
     * @return id of proxy item
     */
    public String getItemId ()
    {
        return this.proxyItemId;
    }

    /**
     * @param listener
     */
    public void setListener ( final ItemUpdateListener listener )
    {
        this.listener = listener;
    }

    /**
     * @param connection
     * @param subscriptionState
     * @param subscriptionError
     */
    public void updateSubscriptionState ( final ProxySubConnectionId connection, final SubscriptionState subscriptionState, final Throwable subscriptionError )
    {
        try
        {
            handleUpdateSubscriptionChange ( connection, subscriptionState, subscriptionError );
        }
        catch ( final Throwable e )
        {
            logger.error ( String.format ( "Failed to change subscription state of item '%s'", this.proxyItemId ), e );
        }
    }

    /**
     * Handle the actual subscription change and wrap the call
     * @param connection the connection that changed 
     * @param subscriptionState the new state
     * @param subscriptionError the optional error
     */
    private void handleUpdateSubscriptionChange ( final ProxySubConnectionId connection, final SubscriptionState subscriptionState, final Throwable subscriptionError )
    {
        boolean doSend;

        synchronized ( this )
        {
            final DataItemValue div = this.values.get ( this.currentConnection );
            div.setSubscriptionState ( subscriptionState );
            div.setSubscriptionError ( subscriptionError );
            doSend = connection.equals ( this.currentConnection );
        }

        if ( doSend )
        {
            this.listener.notifySubscriptionChange ( subscriptionState, subscriptionError );
        }
    }
}
