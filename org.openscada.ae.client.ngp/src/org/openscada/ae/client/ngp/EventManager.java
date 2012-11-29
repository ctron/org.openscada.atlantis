package org.openscada.ae.client.ngp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ae.client.EventListener;
import org.openscada.ae.data.EventInformation;
import org.openscada.core.data.SubscriptionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventManager
{

    private final static Logger logger = LoggerFactory.getLogger ( EventManager.class );

    private final Map<String, EventListener> eventListeners = new HashMap<String, EventListener> ();

    private final Executor executor;

    private final ConnectionImpl connection;

    public EventManager ( final Executor executor, final ConnectionImpl connection )
    {
        this.executor = executor;
        this.connection = connection;
    }

    public void setEventListener ( final String eventQueryId, final EventListener listener )
    {
        if ( listener == null )
        {
            clearEventListener ( eventQueryId );
        }
        else
        {
            updateEventListener ( eventQueryId, listener );
        }
    }

    private void updateEventListener ( final String eventQueryId, final EventListener listener )
    {
        EventListener oldListener;

        oldListener = this.eventListeners.put ( eventQueryId, listener );
        if ( oldListener == listener )
        {
            return;
        }

        if ( oldListener != null )
        {
            // notify old listener first
            fireEventStatusChange ( oldListener, SubscriptionState.DISCONNECTED );
        }
        else
        {
            // request data
            sendSubscribeEvents ( eventQueryId, true );
        }

        // initially send DISCONNECTED
        fireEventStatusChange ( listener, SubscriptionState.DISCONNECTED );
    }

    private void clearEventListener ( final String eventQueryId )
    {
        EventListener oldListener;

        oldListener = this.eventListeners.remove ( eventQueryId );
        if ( oldListener != null )
        {
            sendSubscribeEvents ( eventQueryId, false );
        }
        if ( oldListener != null )
        {
            fireEventStatusChange ( oldListener, SubscriptionState.DISCONNECTED );
        }
    }

    private void fireEventStatusChange ( final EventListener listener, final SubscriptionState status )
    {
        if ( listener == null )
        {
            return;
        }

        this.executor.execute ( new Runnable () {
            @Override
            public void run ()
            {
                listener.statusChanged ( status );
            }
        } );
    }

    private void sendSubscribeEvents ( final String eventQueryId, final boolean flag )
    {
        logger.info ( "Requesting events: {}/{}", eventQueryId, flag );

        if ( flag )
        {
            this.connection.sendSubscribeEventPool ( eventQueryId );
        }
        else
        {
            this.connection.sendUnsubscribeEventPool ( eventQueryId );
        }
    }

    private void fireEventDataChange ( final EventListener listener, final List<EventInformation> added )
    {
        if ( listener == null )
        {
            logger.warn ( "Event change data without a listener" );
            return;
        }

        try
        {
            logger.debug ( "notify condition data change" );
            this.executor.execute ( new Runnable () {

                @Override
                public void run ()
                {
                    listener.dataChanged ( Events.convertToEvent ( added ) );
                }
            } );

        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to notify", e );
        }
    }

    public void dispose ()
    {
        this.eventListeners.clear ();
    }

    public void onClosed ()
    {
        for ( final EventListener listener : this.eventListeners.values () )
        {
            fireEventStatusChange ( listener, SubscriptionState.DISCONNECTED );
        }
    }

    public void onBound ()
    {
        for ( final String id : this.eventListeners.keySet () )
        {
            this.connection.sendSubscribeMonitorPool ( id );
        }
    }

    public void handleDataUpdate ( final String eventQueryId, final List<EventInformation> added )
    {
        final EventListener listener = this.eventListeners.get ( eventQueryId );
        fireEventDataChange ( listener, added );
    }

    public void handleStatusUpdate ( final String eventQueryId, final SubscriptionState state )
    {
        final EventListener listener = this.eventListeners.get ( eventQueryId );
        fireEventStatusChange ( listener, state );
    }

}
