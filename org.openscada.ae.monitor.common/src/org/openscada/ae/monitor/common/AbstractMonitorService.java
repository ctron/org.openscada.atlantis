package org.openscada.ae.monitor.common;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.ae.ConditionStatus;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.monitor.ConditionListener;
import org.openscada.ae.monitor.MonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMonitorService implements MonitorService
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractStateMachineMonitorService.class );

    protected Set<ConditionListener> conditionListeners = new HashSet<ConditionListener> ();

    private final String id;

    private final Executor executor;

    protected ConditionStatusInformation currentState;

    public AbstractMonitorService ( final String id, final Executor executor )
    {
        this.executor = executor;
        this.id = id;

        this.currentState = new ConditionStatusInformation ( id, ConditionStatus.INIT, new Date (), null, null, null, null );
    }

    public String getId ()
    {
        return this.id;
    }

    public synchronized void addStatusListener ( final ConditionListener listener )
    {
        if ( listener == null )
        {
            return;
        }

        if ( this.conditionListeners.add ( listener ) )
        {
            final ConditionStatusInformation state = this.currentState;
            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    listener.statusChanged ( state );
                }
            } );
        }
    }

    protected synchronized void notifyStateChange ( final ConditionStatusInformation state )
    {
        final ConditionListener[] listeners = this.conditionListeners.toArray ( new ConditionListener[this.conditionListeners.size ()] );

        this.currentState = state;

        this.executor.execute ( new Runnable () {

            public void run ()
            {
                for ( final ConditionListener listener : listeners )
                {
                    try
                    {
                        listener.statusChanged ( state );
                    }
                    catch ( final Throwable e )
                    {
                        logger.warn ( "Failed to notify", e );
                    }
                }
            }
        } );
    }

    public synchronized void removeStatusListener ( final ConditionListener listener )
    {
        this.conditionListeners.remove ( listener );
    }

}
