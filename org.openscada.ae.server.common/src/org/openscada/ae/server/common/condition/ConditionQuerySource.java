package org.openscada.ae.server.common.condition;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.server.ConditionListener;
import org.openscada.core.subscription.SubscriptionInformation;
import org.openscada.core.subscription.SubscriptionSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionQuerySource implements SubscriptionSource, ConditionQueryListener
{
    private final static Logger logger = LoggerFactory.getLogger ( ConditionQuerySource.class );

    private final ConditionQuery conditionQuery;

    private final Set<ConditionListener> listeners = new HashSet<ConditionListener> ();

    private final Map<String, ConditionStatusInformation> cachedData = new HashMap<String, ConditionStatusInformation> ();

    private final String queryId;

    public ConditionQuerySource ( final String queryId, final ConditionQuery conditionQuery )
    {
        this.queryId = queryId;
        this.conditionQuery = conditionQuery;
    }

    public synchronized void addListener ( final Collection<SubscriptionInformation> listeners )
    {
        final boolean wasEmpty = this.listeners.isEmpty ();

        for ( final SubscriptionInformation information : listeners )
        {
            final ConditionListener listener = (ConditionListener)information.getListener ();
            this.listeners.add ( listener );

            if ( !this.cachedData.isEmpty () )
            {
                listener.dataChanged ( this.queryId, this.cachedData.values ().toArray ( new ConditionStatusInformation[0] ), null );
            }
        }

        if ( wasEmpty && !this.listeners.isEmpty () )
        {
            this.conditionQuery.setListener ( this );
        }
    }

    public synchronized void removeListener ( final Collection<SubscriptionInformation> listeners )
    {
        for ( final SubscriptionInformation information : listeners )
        {
            final ConditionListener listener = (ConditionListener)information.getListener ();
            this.listeners.remove ( listener );
        }

        if ( this.listeners.isEmpty () )
        {
            this.conditionQuery.setListener ( null );
            this.cachedData.clear ();
        }
    }

    public boolean supportsListener ( final SubscriptionInformation subscriptionInformation )
    {
        return subscriptionInformation.getListener () instanceof ConditionListener;
    }

    public synchronized void dataChanged ( final ConditionStatusInformation[] addedOrUpdated, final String[] removed )
    {
        if ( removed != null )
        {
            for ( final String id : removed )
            {
                this.cachedData.remove ( id );
            }
        }
        if ( addedOrUpdated != null )
        {
            for ( final ConditionStatusInformation info : addedOrUpdated )
            {
                this.cachedData.put ( info.getId (), info );
            }
        }
        for ( final ConditionListener listener : this.listeners )
        {
            try
            {
                listener.dataChanged ( this.queryId, addedOrUpdated, removed );
            }
            catch ( final Throwable e )
            {
                logger.warn ( "Failed to notify", e );
            }
        }
    }
}
