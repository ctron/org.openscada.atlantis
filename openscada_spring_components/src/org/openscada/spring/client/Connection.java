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

package org.openscada.spring.client;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.core.client.NoConnectionException;
import org.openscada.da.client.ItemManager;
import org.openscada.da.client.WriteOperationCallback;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * <p>A wrapper around the DA connection creation for OpenSCADA</p>
 * <p>
 * The connection is bound to the lifecycle of the bean context. All assigned
 * item adapters will be attached to the connection and a subscription will be
 * requested.
 * </p>
 * @author Jens Reimannn
 *
 */
public class Connection implements InitializingBean, DisposableBean, ConnectionOperations, ConnectionStateListener
{
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
        this.connection.connect ();
        this.itemManager = new ItemManager ( this.connection );
    }

    public void connect ()
    {
        final org.openscada.da.client.Connection connection = this.connection;
        if ( connection != null )
        {
            connection.connect ();
        }
    }

    public void disconnect ()
    {
        final org.openscada.da.client.Connection connection = this.connection;
        if ( connection != null )
        {
            connection.disconnect ();
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
        this.itemManager = null;
        this.connection.disconnect ();
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
