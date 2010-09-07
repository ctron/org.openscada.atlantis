/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.da.server.proxy.connection;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.openscada.core.Variant;
import org.openscada.core.client.Connection;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.proxy.item.ProxyDataItem;

/**
 * encapsulates notification of items in case of a connection error
 * 
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 */
public class NotifyConnectionErrorListener implements ConnectionStateListener
{
    private final ProxyGroup proxyConnection;

    private ConnectionState currentState;

    private Timer timer;

    /**
     * needs connection to iterate over registered items
     * @param proxyConnection
     */
    public NotifyConnectionErrorListener ( final ProxyGroup proxyConnection )
    {
        this.proxyConnection = proxyConnection;
    }

    /**
     * either sends connection state straight away, or schedules it for later execution
     * @see org.openscada.core.client.ConnectionStateListener#stateChange(org.openscada.core.client.Connection, org.openscada.core.client.ConnectionState, java.lang.Throwable)
     */
    public void stateChange ( final Connection connection, final ConnectionState state, final Throwable error )
    {
        this.currentState = state;
        if ( this.proxyConnection.getWait () > 0 && !ConnectionState.BOUND.equals ( state ) )
        {
            scheduleItemUpdate ( state );
        }
        else
        {
            sendConnectionState ( state );
        }
    }

    /**
     * schedules sending of connections state to registered items
     * connection state is only sent if it differs from last one
     * @param state
     */
    private void scheduleItemUpdate ( final ConnectionState state )
    {
        synchronized ( this )
        {
            if ( this.timer == null )
            {
                this.timer = new Timer ();
                this.timer.schedule ( new TimerTask () {
                    @Override
                    public void run ()
                    {
                        if ( !state.equals ( NotifyConnectionErrorListener.this.currentState ) )
                        {
                            sendConnectionState ( NotifyConnectionErrorListener.this.currentState );
                        }
                        NotifyConnectionErrorListener.this.timer = null;
                    }
                }, this.proxyConnection.getWait () );
            }
        }
    }

    /**
     * sends error attribute to all registered items 
     * @param state
     */
    private void sendConnectionState ( final ConnectionState state )
    {
        for ( final ProxyDataItem item : this.proxyConnection.getRegisteredItems ().values () )
        {
            final Map<String, Variant> attrs = new HashMap<String, Variant> ();
            if ( ConnectionState.BOUND.equals ( state ) )
            {
                attrs.put ( "proxy.error", null );
                attrs.put ( "proxy.error.message", null );
            }
            else
            {
                attrs.put ( "proxy.error", Variant.TRUE );
                attrs.put ( "proxy.error.message", new Variant ( "Underlying connection in state " + state ) );
            }
            item.updateData ( null, attrs, AttributeMode.UPDATE );
        }
    }
}
