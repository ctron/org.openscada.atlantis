package org.openscada.ae.monitor.common;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.ConditionListener;
import org.openscada.ae.monitor.MonitorService;
import org.openscada.ae.monitor.common.handler.StateHandler;
import org.openscada.ae.monitor.common.handler.impl.InitHandler;
import org.openscada.core.Variant;
import org.openscada.sec.UserInformation;

public class AbstractMonitorService implements MonitorService
{
    private final static Logger logger = Logger.getLogger ( AbstractMonitorService.class );

    protected Set<ConditionListener> conditionListeners = new CopyOnWriteArraySet<ConditionListener> ();

    private final String id;

    private StateHandler handler;

    private final EventProcessor eventProcessor;

    private volatile ConditionStatusInformation currentStatus;

    public AbstractMonitorService ( final EventProcessor eventProcessor, final String id )
    {
        this.id = id;
        this.eventProcessor = eventProcessor;
    }

    public synchronized void init ()
    {
        if ( this.handler == null )
        {
            setHandler ( new InitHandler ( this ) );
        }
    }

    /**
     * Set a new state handler
     * @param handler the new handler
     */
    public void setHandler ( final StateHandler handler )
    {
        synchronized ( this )
        {
            if ( this.handler != null )
            {
                this.handler.deactivate ();
            }
            this.handler = handler;
            if ( this.handler != null )
            {
                this.handler.activate ();
            }
        }
        this.currentStatus = handler.getState ();
        notifyStateChange ( this.currentStatus );
    }

    public void addStatusListener ( final ConditionListener listener )
    {
        if ( listener != null )
        {
            this.conditionListeners.add ( listener );
            listener.statusChanged ( this.currentStatus );
        }
    }

    public void removeStatusListener ( final ConditionListener listener )
    {
        this.conditionListeners.remove ( listener );
    }

    protected synchronized void setFailure ( final Variant value, final Date timestamp )
    {
        this.handler.fail ( value, timestamp );
    }

    protected synchronized void setOk ( final Variant value, final Date timestamp )
    {
        this.handler.ok ( value, timestamp );
    }

    protected synchronized void setUnsafe ()
    {
        this.handler.unsafe ();
    }

    public synchronized void setActive ( final boolean state )
    {
        if ( state )
        {
            this.handler.enable ();
        }
        else
        {
            this.handler.disable ();
        }
    }

    public synchronized void akn ( final UserInformation userInformation, final Date aknTimestamp )
    {
        this.handler.akn ( userInformation, aknTimestamp );
    }

    public synchronized void setRequireAkn ( final boolean state )
    {
        if ( state )
        {
            this.handler.requireAkn ();
        }
        else
        {
            this.handler.ignoreAkn ();
        }
    }

    protected void notifyStateChange ( final ConditionStatusInformation status )
    {
        for ( final ConditionListener listener : this.conditionListeners )
        {
            try
            {
                listener.statusChanged ( status );
            }
            catch ( final Throwable e )
            {
                logger.warn ( "Failed to notify", e );
            }
        }
    }

    public String getId ()
    {
        return this.id;
    }

    public void publishEvent ( final Event event )
    {
        final EventBuilder builder = Event.create ();
        builder.event ( event );

        injectEventAttributes ( builder );

        this.eventProcessor.publishEvent ( builder.build () );
    }

    protected void injectEventAttributes ( final EventBuilder builder )
    {
    }

}
