package org.openscada.ae.server.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.ae.BrowserEntry;
import org.openscada.ae.BrowserListener;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.Event;
import org.openscada.ae.server.ConditionListener;
import org.openscada.ae.server.EventListener;
import org.openscada.ae.server.Session;
import org.openscada.core.server.common.session.AbstractSessionImpl;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.sec.UserInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionImpl extends AbstractSessionImpl implements Session, BrowserListener
{

    private final static Logger logger = LoggerFactory.getLogger ( SessionImpl.class );

    private final EventListener eventListener;

    private final ConditionListener conditionListener;

    private volatile ConditionListener clientConditionListener;

    private volatile EventListener clientEventListener;

    private volatile BrowserListener clientBrowserListener;

    private final Map<String, BrowserEntry> browserCache = new HashMap<String, BrowserEntry> ();

    private boolean disposed = false;

    private final Set<QueryImpl> queries = new HashSet<QueryImpl> ();

    public SessionImpl ( final UserInformation userInformation )
    {
        super ( userInformation );
        logger.info ( "Created new session" );

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

    public synchronized void dispose ()
    {
        logger.info ( "Disposing session" );

        // mark disposed
        this.disposed = true;

        // dispose queries : operate on copy to prevent concurrent modification
        for ( final QueryImpl query : new ArrayList<QueryImpl> ( this.queries ) )
        {
            query.dispose ();
        }
        this.queries.clear ();

        // clear listeners
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

    public synchronized void addQuery ( final QueryImpl query )
    {
        if ( this.disposed )
        {
            query.dispose ();
        }
        else
        {
            this.queries.add ( query );
        }
    }

    public synchronized void removeQuery ( final QueryImpl query )
    {
        this.queries.remove ( query );
    }
}
