/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.opc2.connection;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.jinterop.dcom.core.JISession;
import org.openscada.opc.dcom.common.impl.OPCCommon;
import org.openscada.opc.dcom.da.OPCSERVERSTATUS;
import org.openscada.opc.dcom.da.impl.OPCGroupStateMgt;
import org.openscada.opc.dcom.da.impl.OPCItemMgt;
import org.openscada.opc.dcom.da.impl.OPCServer;
import org.openscada.opc.dcom.da.impl.OPCSyncIO;

public class OPCModel
{
    private boolean connectionRequested;
    private boolean connecting;
    private JISession session;
    private OPCServer server;
    private long lastConnect;
    private long reconnectDelay = 5000;
    private OPCSERVERSTATUS serverState;
    private OPCGroupStateMgt group;
    private OPCItemMgt itemMgt;
    private OPCSyncIO syncIo;
    private OPCCommon common;
    private Throwable lastConnectionError;
    private ConnectionState connectionState = ConnectionState.DISCONNECTED;
    private Set<Thread> disposersRunning = new CopyOnWriteArraySet<Thread> ();
    private ControllerState controllerState = ControllerState.IDLE;
    private long loopDelay = 250;

    private PropertyChangeSupport listeners = new PropertyChangeSupport ( this );

    public void addListener ( PropertyChangeListener listener )
    {
        listeners.addPropertyChangeListener ( listener );
    }

    public void removeListener ( PropertyChangeListener listener )
    {
        listeners.removePropertyChangeListener ( listener );
    }

    public void addListener ( String propertyName, PropertyChangeListener listener )
    {
        listeners.addPropertyChangeListener ( propertyName, listener );
    }

    public void removeListener ( String propertyName, PropertyChangeListener listener )
    {
        listeners.removePropertyChangeListener ( propertyName, listener );
    }

    public void setLastConnectNow ()
    {
        setLastConnect ( System.currentTimeMillis () );
    }

    public void setLastConnect ( long lastConnect )
    {
        long oldLastConnect = this.lastConnect;
        this.lastConnect = lastConnect;
        this.listeners.firePropertyChange ( "lastConnect", oldLastConnect, lastConnect );
    }

    public long getLastConnect ()
    {
        return this.lastConnect;
    }

    public boolean mayConnect ()
    {
        return System.currentTimeMillis () - lastConnect > reconnectDelay;
    }

    public boolean isConnected ()
    {
        return session != null && server != null;
    }

    public JISession getSession ()
    {
        return session;
    }

    public void setSession ( JISession session )
    {
        JISession oldSession = this.session;
        boolean oldConnected = isConnected ();

        this.session = session;

        this.listeners.firePropertyChange ( "session", oldSession, session );
        this.listeners.firePropertyChange ( "connected", oldConnected, isConnected () );
    }

    public OPCServer getServer ()
    {
        return server;
    }

    public void setServer ( OPCServer server )
    {
        OPCServer oldServer = this.server;
        boolean oldConnected = isConnected ();

        this.server = server;

        this.listeners.firePropertyChange ( "session", oldServer, session );
        this.listeners.firePropertyChange ( "connected", oldConnected, isConnected () );
    }

    public boolean isConnectionRequested ()
    {
        return connectionRequested;
    }

    public void setConnectionRequested ( boolean connectionRequested )
    {
        this.connectionRequested = connectionRequested;
    }

    public boolean isConnecting ()
    {
        return connecting;
    }

    public void setConnecting ( boolean connecting )
    {
        boolean oldConnecting = this.connecting;
        this.connecting = connecting;
        this.listeners.firePropertyChange ( "connecting", oldConnecting, connecting );
    }

    public long getReconnectDelay ()
    {
        return reconnectDelay;
    }

    public void setReconnectDelay ( long reconnectDelay )
    {
        this.reconnectDelay = reconnectDelay;
    }

    public OPCSERVERSTATUS getServerState ()
    {
        return serverState;
    }

    public void setServerState ( OPCSERVERSTATUS serverState )
    {
        OPCSERVERSTATUS oldServerState = this.serverState;
        this.serverState = serverState;
        listeners.firePropertyChange ( "serverState", oldServerState, serverState );
    }

    public OPCGroupStateMgt getGroup ()
    {
        return group;
    }

    public void setGroup ( OPCGroupStateMgt group )
    {
        this.group = group;
    }

    public OPCItemMgt getItemMgt ()
    {
        return itemMgt;
    }

    public void setItemMgt ( OPCItemMgt itemMgt )
    {
        this.itemMgt = itemMgt;
    }

    public OPCSyncIO getSyncIo ()
    {
        return syncIo;
    }

    public void setSyncIo ( OPCSyncIO syncIo )
    {
        this.syncIo = syncIo;
    }

    public OPCCommon getCommon ()
    {
        return common;
    }

    public void setCommon ( OPCCommon common )
    {
        this.common = common;
    }

    public Throwable getLastConnectionError ()
    {
        return lastConnectionError;
    }

    public void setLastConnectionError ( Throwable lastConnectionError )
    {
        Throwable oldLastConnectionError = this.lastConnectionError;
        this.lastConnectionError = lastConnectionError;
        this.listeners.firePropertyChange ( "lastConnectionError", oldLastConnectionError, lastConnectionError );
    }

    public ConnectionState getConnectionState ()
    {
        return this.connectionState;
    }

    public void setConnectionState ( ConnectionState connectionState )
    {
        ConnectionState oldConnectionState = this.connectionState;
        this.connectionState = connectionState;
        this.listeners.firePropertyChange ( "connectionState", oldConnectionState, connectionState );
    }

    public long getNumDisposersRunning ()
    {
        return this.disposersRunning.size ();
    }

    public void addDisposerRunning ( Thread disposer )
    {
        long oldDisposersRunning;
        long disposersRunning;

        synchronized ( this.disposersRunning )
        {
            oldDisposersRunning = this.disposersRunning.size ();
            this.disposersRunning.add ( disposer );
            disposersRunning = this.disposersRunning.size ();
        }
        this.listeners.firePropertyChange ( "numDisposersRunning", oldDisposersRunning, disposersRunning );
    }

    public void removeDisposerRunning ( Thread disposer )
    {
        long oldDisposersRunning;
        long disposersRunning;

        synchronized ( this.disposersRunning )
        {
            oldDisposersRunning = this.disposersRunning.size ();
            this.disposersRunning.remove ( disposer );
            disposersRunning = this.disposersRunning.size ();
        }
        this.listeners.firePropertyChange ( "numDisposersRunning", oldDisposersRunning, disposersRunning );
    }

    public ControllerState getControllerState ()
    {
        return controllerState;
    }

    public void setControllerState ( ControllerState controllerState )
    {
        ControllerState oldControllerState = this.controllerState;
        this.controllerState = controllerState;
        this.listeners.firePropertyChange ( "controllerState", oldControllerState, controllerState );
    }

    public long getLoopDelay ()
    {
        return loopDelay;
    }

    public void setLoopDelay ( long loopDelay )
    {
        long oldLoopDelay = this.loopDelay;
        this.loopDelay = loopDelay;
        this.listeners.firePropertyChange ( "loopDelay", oldLoopDelay, loopDelay );
    }
}
