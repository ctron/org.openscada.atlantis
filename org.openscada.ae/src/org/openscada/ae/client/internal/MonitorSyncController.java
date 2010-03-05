package org.openscada.ae.client.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.client.ConditionListener;
import org.openscada.ae.client.Connection;
import org.openscada.core.subscription.SubscriptionState;

public class MonitorSyncController implements ConditionListener
{
    private final List<ConditionListener> listeners = new CopyOnWriteArrayList<ConditionListener> ();

    private final Connection connection;

    private final String id;

    private final Set<ConditionStatusInformation> cachedMonitors = Collections.<ConditionStatusInformation> newSetFromMap ( new ConcurrentHashMap<ConditionStatusInformation, Boolean> () );

    public MonitorSyncController ( final Connection connection, final String id )
    {
        if ( connection == null )
        {
            throw new IllegalArgumentException ( "connection is null" );
        }
        this.connection = connection;
        this.id = id;
    }

    public void dataChanged ( final ConditionStatusInformation[] addedOrUpdated, final String[] removed )
    {
        if ( addedOrUpdated != null )
        {
            this.cachedMonitors.removeAll ( Arrays.asList ( addedOrUpdated ) );
            this.cachedMonitors.addAll ( Arrays.asList ( addedOrUpdated ) );
        }
        if ( removed != null )
        {
            final Set<ConditionStatusInformation> toRemove = new HashSet<ConditionStatusInformation> ();
            List<String> removedList = Arrays.asList ( removed );
            for ( ConditionStatusInformation monitor : this.cachedMonitors )
            {
                if ( removedList.contains ( monitor.getId () ) )
                {
                    toRemove.add ( monitor );
                }
            }
            for ( ConditionStatusInformation monitor : toRemove )
            {
                this.cachedMonitors.remove ( monitor );
            }
        }
        for ( ConditionListener listener : this.listeners )
        {
            listener.dataChanged ( addedOrUpdated, removed );
        }
    }

    public synchronized void addListener ( final ConditionListener listener )
    {
        this.listeners.add ( listener );
        listener.dataChanged ( this.cachedMonitors.toArray ( new ConditionStatusInformation[] {} ), null );
        if ( this.listeners.size () == 1 )
        {
            this.connection.setConditionListener ( this.id, this );
        }
    }

    public synchronized boolean removeListener ( final ConditionListener listener )
    {
        this.listeners.remove ( listener );
        if ( this.listeners.size () == 0 )
        {
            this.connection.setConditionListener ( this.id, listener );
            this.cachedMonitors.clear ();
            return true;
        }
        return false;
    }

    public void statusChanged ( final SubscriptionState state )
    {
        switch ( state )
        {
        case CONNECTED:
            for ( ConditionListener listener : this.listeners )
            {
                listener.dataChanged ( this.cachedMonitors.toArray ( new ConditionStatusInformation[] {} ), null );
            }
            break;
        default:
            break;
        }
    }
}
