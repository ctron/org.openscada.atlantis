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

package org.openscada.spring.client.redundancy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.openscada.core.NullValueException;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.NoConnectionException;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.spring.client.command.DataItemCommand;
import org.openscada.spring.client.event.ItemEventAdapter;
import org.openscada.spring.client.event.ItemEventListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class RedundancySwitchHandler implements InitializingBean
{
    private static Logger logger = Logger.getLogger ( RedundancySwitchHandler.class );

    protected final Map<String, Boolean> lastConnectionStates = Collections.synchronizedMap ( new HashMap<String, Boolean> () );

    protected final Map<String, Boolean> lastMasterFlags = Collections.synchronizedMap ( new HashMap<String, Boolean> () );

    protected Map<String, ItemEventAdapter> masterFlagItems;

    protected Map<String, ItemEventAdapter> connectedFlagItems;

    protected DataItemCommand redundancySwitcherCommand;

    protected ItemEventAdapter redundancySwitcherItem;

    protected Long lastSwitchOccured = 0L;

    protected String currentConnection = null;

    protected Timer switchTimer = new Timer ();

    protected Boolean automatic = true;

    public void afterPropertiesSet () throws Exception
    {
        Assert.notNull ( this.redundancySwitcherCommand, "'redundancySwitcher' must not be null" );
        Assert.notNull ( this.masterFlagItems, "'masterFlagItems' must not be null" );
        if ( Long.valueOf ( 0 ).equals ( this.lastSwitchOccured ) )
        {
            this.lastSwitchOccured = System.currentTimeMillis ();
        }

        // first evaluate master flags
        for ( final Entry<String, ItemEventAdapter> masterFlagItem : this.masterFlagItems.entrySet () )
        {
            masterFlagItem.getValue ().attachTarget ( new ItemEventListener () {

                public void itemEvent ( final String topic, final DataItemValue value )
                {
                    if ( value == null )
                    {
                        logger.info ( "Master flag changed for " + masterFlagItem.getKey () + " to " + value );
                        RedundancySwitchHandler.this.lastMasterFlags.put ( masterFlagItem.getKey (), false );
                        return;
                    }
                    logger.info ( "Master flag changed for " + masterFlagItem.getKey () + " to " + value + " subscription state: " + value.getSubscriptionState () );
                    RedundancySwitchHandler.this.lastConnectionStates.put ( masterFlagItem.getKey (), SubscriptionState.CONNECTED.equals ( value.getSubscriptionState () ) );
                    RedundancySwitchHandler.this.lastMasterFlags.put ( masterFlagItem.getKey (), value.getValue ().asBoolean () );
                    if ( value.isError () )
                    {
                        logger.info ( "Master flag " + masterFlagItem.getKey () + " has error" );
                        RedundancySwitchHandler.this.lastConnectionStates.put ( masterFlagItem.getKey (), false );
                        RedundancySwitchHandler.this.lastMasterFlags.put ( masterFlagItem.getKey (), false );
                    }
                    switchConnection ();
                }
            } );
        }

        // 2nd evaluate if there is a connection at all (e.g. OPC connection)
        for ( final Entry<String, ItemEventAdapter> connectedFlagItem : this.connectedFlagItems.entrySet () )
        {
            connectedFlagItem.getValue ().attachTarget ( new ItemEventListener () {

                public void itemEvent ( final String topic, final DataItemValue value )
                {
                    if ( value == null )
                    {
                        logger.info ( "Connected flag changed for " + connectedFlagItem.getKey () + " to " + value );
                        RedundancySwitchHandler.this.lastMasterFlags.put ( connectedFlagItem.getKey (), false );
                        RedundancySwitchHandler.this.lastConnectionStates.put ( connectedFlagItem.getKey (), false );
                    }
                    else if ( value.getValue ().asBoolean () )
                    {
                        logger.info ( "Connected flag changed for " + connectedFlagItem.getKey () + " to " + value + " subscription state: " + value.getSubscriptionState () );
                        RedundancySwitchHandler.this.lastConnectionStates.put ( connectedFlagItem.getKey (), true );
                    }
                    else
                    {
                        logger.info ( "Connected flag changed for " + connectedFlagItem.getKey () + " to " + value + " subscription state: " + value.getSubscriptionState () );
                        RedundancySwitchHandler.this.lastConnectionStates.put ( connectedFlagItem.getKey (), false );
                        RedundancySwitchHandler.this.lastMasterFlags.put ( connectedFlagItem.getKey (), false );
                    }
                    switchConnection ();
                }
            } );
        }

        this.redundancySwitcherItem.attachTarget ( new ItemEventListener () {
            public void itemEvent ( final String topic, final DataItemValue value )
            {
                logger.info ( "Current redundant connection changed to " + value );
                try
                {
                    RedundancySwitchHandler.this.currentConnection = value.getValue ().asString ();
                }
                catch ( final NullValueException e )
                {
                    RedundancySwitchHandler.this.currentConnection = null;
                }
            };
        } );
    }

    protected void switchConnection ()
    {
        // automatic switch is not wanted, therefore don't switch automatically
        if ( !this.automatic )
        {
            logger.info ( "switch to new connection was requested, but automatic switching is not active" );
            return;
        }
        synchronized ( this )
        {
            // first find available Connection
            final List<String> availableConnections = new ArrayList<String> ();
            for ( final Entry<String, Boolean> connectionEntry : this.lastConnectionStates.entrySet () )
            {
                if ( connectionEntry.getValue () )
                {
                    availableConnections.add ( connectionEntry.getKey () );
                }
            }
            // find master for all active connections
            final List<String> availableMasters = new ArrayList<String> ();
            for ( final Entry<String, Boolean> masterFlagEntry : this.lastMasterFlags.entrySet () )
            {
                if ( !availableConnections.contains ( masterFlagEntry.getKey () ) )
                {
                    continue;
                }
                if ( masterFlagEntry.getValue () )
                {
                    availableMasters.add ( masterFlagEntry.getKey () );
                }
            }
            // if currentConnection is available anyway, just stay on current 
            // connection
            if ( availableMasters.contains ( this.currentConnection ) )
            {
                logger.info ( "curent master is already active connection" );
                return;
            }
            // if a master is available at all switch to the first one found
            if ( availableMasters.size () > 0 )
            {
                final String newMaster = availableMasters.get ( 0 );
                try
                {
                    switchTo ( newMaster );
                    return;
                }
                catch ( final NoConnectionException e )
                {
                    logger.warn ( "was not able to switch to new Master " + newMaster + " because: " + e.getMessage () );
                }
                catch ( final OperationException e )
                {
                    logger.warn ( "was not able to switch to new Master " + newMaster + " because: " + e.getMessage () );
                }
            }
            // if current connection is active even though not master use it anyway
            if ( availableConnections.contains ( this.currentConnection ) )
            {
                logger.info ( "no master available but current connection exists, therefore staying on " + this.currentConnection );
                return;
            }
            // in any other case, just use the first available connection 
            if ( availableConnections.size () > 0 )
            {
                final String newConnection = availableConnections.get ( 0 );
                try
                {
                    switchTo ( newConnection );
                    return;
                }
                catch ( final NoConnectionException e )
                {
                    logger.warn ( "was not able to switch to new Connection " + newConnection + " because: " + e.getMessage () );
                }
                catch ( final OperationException e )
                {
                    logger.warn ( "was not able to switch to new Connection " + newConnection + " because: " + e.getMessage () );
                }
            }
        }
        logger.error ( "switch was called, but falled through, which should not happen, except no connection is actually available!" );
    }

    /**
     * switch explicitly to new connection (only advisable to use in manual 
     * mode)
     * @param connectionId
     * @throws NoConnectionException
     * @throws OperationException
     */
    public void switchTo ( final String connectionId ) throws NoConnectionException, OperationException
    {
        logger.info ( "switching to new Master " + connectionId );
        this.redundancySwitcherCommand.command ( new Variant ( connectionId ) );
        this.lastSwitchOccured = System.currentTimeMillis ();
    }

    /**
     * @return Map of master flags (id : item)
     */
    public Map<String, ItemEventAdapter> getMasterFlagItems ()
    {
        return this.masterFlagItems;
    }

    /**
     * @return command to switch redundant connection
     */
    public DataItemCommand getRedundancySwitcherCommand ()
    {
        return this.redundancySwitcherCommand;
    }

    /**
     * @return redundancySwitcherItem
     */
    public ItemEventAdapter getRedundancySwitcherItem ()
    {
        return this.redundancySwitcherItem;
    }

    /**
     * @param redundancySwitcherItem
     */
    public void setRedundancySwitcherItem ( final ItemEventAdapter redundancySwitcherItem )
    {
        this.redundancySwitcherItem = redundancySwitcherItem;
    }

    /**
     * @param masterFlagItems
     */
    public void setMasterFlagItems ( final Map<String, ItemEventAdapter> masterFlagItems )
    {
        this.masterFlagItems = masterFlagItems;
    }

    /**
     * @return
     */
    public Map<String, ItemEventAdapter> getConnectedFlagItems ()
    {
        return this.connectedFlagItems;
    }

    /**
     * @param connectedFlagItems
     */
    public void setConnectedFlagItems ( final Map<String, ItemEventAdapter> connectedFlagItems )
    {
        this.connectedFlagItems = connectedFlagItems;
    }

    /**
     * @param redundancySwitcher
     */
    public void setRedundancySwitcherCommand ( final DataItemCommand redundancySwitcher )
    {
        this.redundancySwitcherCommand = redundancySwitcher;
    }

    /**
     * @return should switching occur automatically
     */
    public Boolean getAutomatic ()
    {
        return this.automatic;
    }

    /**
     * @param automatic should switching occur automatically
     */
    public void setAutomatic ( final Boolean automatic )
    {
        this.automatic = automatic;
    }
}
