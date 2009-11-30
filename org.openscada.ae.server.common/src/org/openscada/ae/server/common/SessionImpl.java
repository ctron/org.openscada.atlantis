package org.openscada.ae.server.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.ae.BrowserEntry;
import org.openscada.ae.BrowserListener;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.Event;
import org.openscada.ae.server.ConditionListener;
import org.openscada.ae.server.EventListener;
import org.openscada.ae.server.Session;
import org.openscada.core.subscription.SubscriptionState;

public class SessionImpl implements Session, BrowserListener
{
    private final static Logger logger = Logger.getLogger ( SessionImpl.class );

    private final EventListener eventListener;

    private final ConditionListener conditionListener;

    private ConditionListener clientConditionListener;

    private EventListener clientEventListener;

    private volatile BrowserListener clientBrowserListener;

    private final Map<String, BrowserEntry> browserCache = new HashMap<String, BrowserEntry> ();

    private final String user;

    public SessionImpl ( final String user )
    {
        logger.info ( "Created new session" );

        this.user = user;

        this.eventListener = new EventListener () {

            public void dataChanged ( final String poolId, final Event[] addedEvents )
            {
                SessionImpl.this.eventDataChanged ( poolId, addedEvents );
            }

            public void updateStatus ( final Object poolId, final SubscriptionState state )
            {
                SessionImpl.this.eventStatusChanged ( poolId.toString (), state );
            }
        };
        this.conditionListener = new ConditionListener () {

            public void dataChanged ( final String subscriptionId, final ConditionStatusInformation[] addedOrUpdated, final String[] removed )
            {
                SessionImpl.this.conditionDataChanged ( subscriptionId, addedOrUpdated, removed );
            }

            public void updateStatus ( final Object poolId, final SubscriptionState state )
            {
                SessionImpl.this.conditionStatusChanged ( poolId.toString (), state );
            }
        };
    }

    protected void conditionStatusChanged ( final String string, final SubscriptionState state )
    {
        final ConditionListener listener = this.clientConditionListener;
        if ( listener != null )
        {
            listener.updateStatus ( string, state );
        }
    }

    protected void conditionDataChanged ( final String subscriptionId, final ConditionStatusInformation[] addedOrUpdated, final String[] removed )
    {
        final ConditionListener listener = this.clientConditionListener;
        if ( listener != null )
        {
            logger.info ( String.format ( "Condition Data Change: %s - %s - %s", subscriptionId, addedOrUpdated != null ? addedOrUpdated.length : "none", removed != null ? removed.length : "none" ) );
            listener.dataChanged ( subscriptionId, addedOrUpdated, removed );
        }
    }

    protected void eventStatusChanged ( final String string, final SubscriptionState state )
    {
        final EventListener listener = this.clientEventListener;
        if ( listener != null )
        {
            listener.updateStatus ( string, state );
        }
    }

    protected void eventDataChanged ( final String poolId, final Event[] addedEvents )
    {
        final EventListener listener = this.clientEventListener;
        if ( listener != null )
        {
            listener.dataChanged ( poolId, addedEvents );
        }
    }

    public void setConditionListener ( final ConditionListener listener )
    {
        this.clientConditionListener = listener;
    }

    public void setEventListener ( final EventListener listener )
    {
        this.clientEventListener = listener;
    }

    public void dispose ()
    {
        logger.info ( "Disposing session" );
        this.clientConditionListener = null;
        this.clientEventListener = null;
        this.clientBrowserListener = null;
    }

    public ConditionListener getConditionListener ()
    {
        return this.conditionListener;
    }

    public EventListener getEventListener ()
    {
        return this.eventListener;
    }

    public void setBrowserListener ( final BrowserListener listener )
    {
        synchronized ( this.browserCache )
        {
            this.clientBrowserListener = listener;
            if ( this.clientBrowserListener != null )
            {
                this.clientBrowserListener.dataChanged ( this.browserCache.values ().toArray ( new BrowserEntry[0] ), null, true );
            }
        }
    }

    public void dataChanged ( final BrowserEntry[] addedOrUpdated, final String[] removed, final boolean full )
    {
        synchronized ( this.browserCache )
        {
            if ( full )
            {
                this.browserCache.clear ();
            }
            if ( removed != null )
            {
                for ( final String id : removed )
                {
                    this.browserCache.remove ( id );
                }
            }
            if ( addedOrUpdated != null )
            {
                for ( final BrowserEntry entry : addedOrUpdated )
                {
                    this.browserCache.put ( entry.getId (), entry );
                }
            }

            final BrowserListener listener = this.clientBrowserListener;
            if ( listener != null )
            {
                listener.dataChanged ( addedOrUpdated, removed, full );
            }
        }
    }

    public String getCurrentUser ()
    {
        return this.user;
    }
}
