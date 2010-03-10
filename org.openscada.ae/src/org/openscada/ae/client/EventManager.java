package org.openscada.ae.client;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ae.client.internal.EventSyncController;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;

public class EventManager implements ConnectionStateListener
{
    private final Connection connection;

    private boolean connected;

    private final Map<String, EventSyncController> eventListeners = new HashMap<String, EventSyncController> ();

    public EventManager ( final Connection connection )
    {
        super ();
        if ( connection == null )
        {
            throw new IllegalArgumentException ( "connection is null" );
        }
        this.connection = connection;

        synchronized ( this )
        {
            this.connection.addConnectionStateListener ( this );
            this.connected = this.connection.getState () == ConnectionState.BOUND;
        }
    }

    public void stateChange ( final org.openscada.core.client.Connection connection, final ConnectionState state, final Throwable error )
    {
        switch ( state )
        {
        case BOUND:
            if ( !this.connected )
            {
                this.connected = true;
            }
            break;
        case CLOSED:
            for ( EventSyncController controller : this.eventListeners.values () )
            {
                controller.dispose ();
            }
            this.eventListeners.clear ();
        default:
            if ( this.connected )
            {
                this.connected = false;
            }
            break;
        }
    }

    public synchronized void addEventListener ( final String id, final EventListener listener )
    {
        EventSyncController eventSyncController = this.eventListeners.get ( id );
        if ( eventSyncController == null )
        {
            eventSyncController = new EventSyncController ( this.connection, id );
            this.eventListeners.put ( id, eventSyncController );
        }
        eventSyncController.addListener ( listener );
    }

    public synchronized void removeEventListener ( final String id, final EventListener listener )
    {
        EventSyncController eventSyncController = this.eventListeners.get ( id );
        if ( eventSyncController == null )
        {
            return;
        }
        eventSyncController.removeListener ( listener );
    }

    public boolean isConnected ()
    {
        return this.connected;
    }
}
