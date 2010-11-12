/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.spring.client;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.AutoReconnectController;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.core.client.NoConnectionException;
import org.openscada.da.client.ItemManager;
import org.openscada.da.client.WriteAttributeOperationCallback;
import org.openscada.da.client.WriteOperationCallback;
import org.openscada.da.core.WriteAttributeResults;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * <p>A wrapper around the DA connection creation for OpenSCADA</p>
 * <p>
 * The connection is bound to the lifecycle of the bean context. All assigned
 * item adapters will be attached to the connection and a subscription will be
 * requested.
 * </p>
 * <p>
 * By default the connection will be kept open using a {@link AutoReconnectController}. Use the property {@link #keepOpen}
 * to change this behavior.
 * </p>
 * @author Jens Reimannn
 *
 */
public class Connection implements InitializingBean, DisposableBean, ConnectionOperations, ConnectionStateListener
{
    /**
     * The default reconnect delay: 10s
     */
    private static final long DEFAULT_RECONNECT_DELAY = 10000;

    private static Logger log = Logger.getLogger ( Connection.class );

    private static Logger logWrite = Logger.getLogger ( Connection.class.getName () + ".write" );

    private String className;

    /**
     * The connection information object
     */
    private ConnectionInformation connectionInformation;

    /**
     * The connection itself
     */
    private org.openscada.da.client.Connection connection;

    /**
     * The item manager handling all item subscriptions
     */
    private ItemManager itemManager;

    /**
     * A flag that indicates if the connection should be kept open using
     * a {@link AutoReconnectController}.
     */
    private boolean keepOpen = true;

    /**
     * The auto reconnect controller, if used
     */
    private AutoReconnectController reconnectController;

    private long reconnectDelay = DEFAULT_RECONNECT_DELAY;

    public void setReconnectDelay ( final long reconnectDelay )
    {
        this.reconnectDelay = reconnectDelay;
    }

    public void setKeepOpen ( final boolean keepOpen )
    {
        this.keepOpen = keepOpen;
    }

    /**
     * <p>Start the connection</p>
     * <p>
     * If the property <q>auto-reconnect</q> is set then connection will be kept open. If it failes
     * an automated reconnect will be scheduled.
     * </p>
     * @throws ClassNotFoundException 
     */
    public void start () throws ClassNotFoundException
    {
        if ( this.className != null && this.className.length () > 0 )
        {
            log.info ( "Pre-Loading class: " + this.className );
            Class.forName ( this.className );
        }

        this.connection = (org.openscada.da.client.Connection)ConnectionFactory.create ( this.connectionInformation );
        if ( this.connection == null )
        {
            throw new RuntimeException ( "No connection provider found that can handle: " + this.connectionInformation );
        }
        this.connection.addConnectionStateListener ( this );

        if ( this.keepOpen )
        {
            this.reconnectController = new AutoReconnectController ( this.connection, this.reconnectDelay );
        }

        // trigger connect
        connect ();
        this.itemManager = new ItemManager ( this.connection );
    }

    public void connect ()
    {
        if ( this.reconnectController == null )
        {
            this.connection.connect ();
        }
        else
        {
            this.reconnectController.connect ();
        }
    }

    public void disconnect ()
    {
        if ( this.reconnectController == null )
        {
            this.connection.disconnect ();
        }
        else
        {
            this.reconnectController.disconnect ();
        }

    }

    /**
     * <p>Stop the connection</p>
     * <p>
     * This will disconnect the currently established connection and prevent further reconnects.
     * </p>
     */
    public void stop ()
    {
        disconnect ();

        this.reconnectController = null;
        this.itemManager = null;

        this.connection.removeConnectionStateListener ( this );
        this.connection = null;
    }

    public void setConnectionInformation ( final ConnectionInformation connectionInformation )
    {
        this.connectionInformation = connectionInformation;
    }

    /**
     * <p>set the connection information as a connection string</p>
     * <p>
     * A connection string can be <code>da:net://192.168.1.200:1202</code> for a
     * DA connection using the <q>net</q> (aka <q>GMPP</q>) protocol to host
     * <q>192.168.1.200</q> on port <q>1202</q>.
     * </p>
     * @param connectionString The connection string
     */
    public void setConnectionString ( final String connectionString )
    {
        setConnectionInformation ( ConnectionInformation.fromURI ( connectionString ) );
    }

    /**
     * Get the item manager of this connection
     * @return the item manager
     */
    public ItemManager getItemManager ()
    {
        return this.itemManager;
    }

    public void afterPropertiesSet () throws Exception
    {
        if ( this.reconnectDelay <= 0 )
        {
            this.reconnectDelay = DEFAULT_RECONNECT_DELAY;
        }
        start ();
    }

    public void destroy () throws Exception
    {
        stop ();
    }

    public void setClassName ( final String className )
    {
        this.className = className;
    }

    public void writeItem ( final String itemName, final Variant value ) throws NoConnectionException, OperationException
    {
        final org.openscada.da.client.Connection connection = this.connection;
        if ( connection == null )
        {
            throw new NoConnectionException ();
        }

        logWrite.info ( String.format ( "Writing: %s to %s", value, itemName ) );

        connection.write ( itemName, value, new WriteOperationCallback () {

            public void complete ()
            {
                log.debug ( String.format ( "Write operation to %s (%s) completed", itemName, value ) );
            }

            public void error ( final Throwable arg0 )
            {
                log.warn ( String.format ( "Write operation to %s (%s) failed", itemName, value ), arg0 );
            }

            public void failed ( final String arg0 )
            {
                log.warn ( String.format ( "Write operation to %s (%s) failed: %s", itemName, value, arg0 ) );
            }
        } );
    }

    public void writeAttributes ( final String itemName, final Map<String, Variant> attributes ) throws NoConnectionException, OperationException
    {
        final org.openscada.da.client.Connection connection = this.connection;
        if ( connection == null )
        {
            throw new NoConnectionException ();
        }

        logWrite.info ( String.format ( "Writing: %s to %s", attributes, itemName ) );

        connection.writeAttributes ( itemName, attributes, new WriteAttributeOperationCallback () {

            public void complete ( final WriteAttributeResults writeAttributeResults )
            {
                log.debug ( String.format ( "Write attributes operation to %s (%s) completed", itemName, attributes ) );
            }

            public void error ( final Throwable throwable )
            {
                log.warn ( String.format ( "Write attributes operation to %s (%s) failed", itemName, attributes ), throwable );
            }

            public void failed ( final String reason )
            {
                log.warn ( String.format ( "Write operation to %s (%s) failed: %s", itemName, attributes, reason ) );
            }
        } );
    }

    public ConnectionState getConnectionState ()
    {
        final org.openscada.da.client.Connection connection = this.connection;
        if ( connection != null )
        {
            return this.connection.getState ();
        }
        else
        {
            return ConnectionState.CLOSED;
        }
    }

    public String getConnectionStateString ()
    {
        return getConnectionState ().name ();
    }

    public void stateChange ( final org.openscada.core.client.Connection connection, final ConnectionState state, final Throwable error )
    {
        final String message = String.format ( "Connection %s changes status: %s", this.connectionInformation, state );
        if ( error != null )
        {
            log.info ( message, error );
        }
        else
        {
            log.info ( message );
        }
    }

    public org.openscada.da.client.Connection getConnection ()
    {
        return this.connection;
    }
}
