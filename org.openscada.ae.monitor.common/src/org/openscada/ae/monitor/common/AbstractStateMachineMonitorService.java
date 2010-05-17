package org.openscada.ae.monitor.common;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ae.ConditionStatus;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.Event.Fields;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.common.StateInformation.State;
import org.openscada.core.Variant;
import org.openscada.sec.UserInformation;
import org.openscada.utils.collection.MapBuilder;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractStateMachineMonitorService extends AbstractPersistentMonitorService
{
    private final static boolean DEBUG = Boolean.getBoolean ( "debug" );

    private final static Logger logger = LoggerFactory.getLogger ( AbstractMonitorService.class );

    private final static StateInformation defaultState;

    static
    {
        defaultState = new StateInformation ();
        defaultState.setActive ( false );
        defaultState.setState ( State.UNSAFE );
        defaultState.setRequireAck ( true );
        defaultState.setTimestamp ( new Date () );
    }

    private final EventProcessor eventProcessor;

    private StateInformation initialInformation;

    private StateInformation information = new StateInformation ();

    private boolean initSent = false;

    private Map<String, Variant> eventInformationAttributes;

    public AbstractStateMachineMonitorService ( final BundleContext context, final Executor executor, final EventProcessor eventProcessor, final String id )
    {
        super ( id, executor, context );
        this.eventProcessor = eventProcessor;
        this.eventInformationAttributes = Collections.emptyMap ();

        sendDebugMessage ( "Initializing" );
    }

    protected void sendDebugMessage ( final String message )
    {
        if ( !DEBUG )
        {
            return;
        }

        final EventBuilder builder = createEventBuilder ();

        builder.attribute ( Fields.MESSAGE, message );
        builder.attribute ( Fields.EVENT_TYPE, "DEBUG" );

        this.eventProcessor.publishEvent ( builder.build () );
    }

    /**
     * Create an event builder pre-filled with the information we have
     * 
     * @return a new event builder
     */
    protected EventBuilder createEventBuilder ()
    {
        final EventBuilder builder = Event.create ();
        final Date now = new Date ();
        builder.sourceTimestamp ( now );
        builder.entryTimestamp ( now );

        injectEventAttributes ( builder );

        return builder;
    }

    /**
     * Restore from persistent state
     * @param state the persisted state
     */
    protected synchronized void setPersistentState ( final StateInformation state )
    {
        logger.debug ( "Setting persistent state: {}", state );

        final boolean doInit = this.initialInformation == null;

        if ( state == null )
        {
            this.initialInformation = new StateInformation ();
        }
        else
        {
            this.initialInformation = state;
        }

        if ( doInit )
        {
            sendDebugMessage ( String.format ( "Initialize from store - current: %s, stored: %s", this.information, this.initialInformation ) );
            final StateInformation newInformation = defaultState.apply ( this.initialInformation ).apply ( this.information );

            this.information = newInformation;

            // re-apply the current state
            applyState ( this.information );
        }
    }

    @Override
    public void init ()
    {
        super.init ();
    }

    protected void setEventInformationAttributes ( final Map<String, Variant> informationAttributes )
    {
        if ( informationAttributes == null )
        {
            this.eventInformationAttributes = Collections.emptyMap ();
        }
        else
        {
            this.eventInformationAttributes = new HashMap<String, Variant> ( informationAttributes );
        }
    }

    @Override
    protected synchronized void switchToInit ()
    {
        logger.warn ( "Switched back to init: {}", getId () );

        // FIXME: implement
        this.initSent = false;
        this.initialInformation = null;
        applyState ( this.information );
    }

    protected synchronized void setFailure ( final Variant value, final Date timestamp )
    {
        if ( this.information.getState () != null && this.information.getState () == State.FAILED )
        {
            // no change
            return;
        }

        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setState ( State.FAILED );
        newInformation.setValue ( value );
        newInformation.setTimestamp ( timestamp );
        newInformation.setLastFailTimestamp ( timestamp );

        applyAndSendStatus ( newInformation );
    }

    protected synchronized void setOk ( final Variant value, Date timestamp )
    {
        if ( this.information.getState () != null && this.information.getState () == State.OK )
        {
            // no change
            return;
        }

        if ( timestamp == null )
        {
            timestamp = new Date ();
        }

        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setState ( State.OK );
        newInformation.setValue ( value );
        newInformation.setTimestamp ( timestamp );

        applyAndSendStatus ( newInformation );
    }

    protected synchronized void setUnsafe ()
    {
        if ( this.information.getState () != null && this.information.getState () == State.UNSAFE )
        {
            // no change
            return;
        }

        final Date now = new Date ();

        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setState ( State.UNSAFE );
        newInformation.setValue ( null );
        newInformation.setTimestamp ( now );

        applyAndSendStatus ( newInformation );
    }

    public synchronized void setActive ( final boolean state )
    {
        if ( this.information.getActive () != null && this.information.getActive () == state )
        {
            // no change
            return;
        }
        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setActive ( state );

        publishEvent ( createEvent ( new Date (), null, "CFG", Variant.valueOf ( state ) ).attribute ( Fields.MESSAGE, "Change active state" ) );
        applyAndSendStatus ( newInformation );
    }

    public synchronized void akn ( final UserInformation userInformation, final Date aknTimestamp )
    {
        if ( !ackPending ( this.information ) )
        {
            return;
        }

        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setLastAckTimestamp ( aknTimestamp );
        newInformation.setLastAckUser ( getUserName ( userInformation ) );
        newInformation.setTimestamp ( aknTimestamp );

        publishEvent ( createEvent ( aknTimestamp, userInformation, "AKN", null ) );
        applyAndSendStatus ( newInformation );
    }

    public synchronized void setRequireAkn ( final boolean state )
    {
        if ( this.information.getRequireAck () != null && this.information.getRequireAck () == state )
        {
            // no change
            return;
        }

        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setRequireAck ( state );

        publishEvent ( createEvent ( new Date (), null, "CFG", Variant.valueOf ( state ) ).attribute ( Fields.MESSAGE, "Change require acknowledge state" ) );
        applyAndSendStatus ( newInformation );
    }

    private String getUserName ( final UserInformation userInformation )
    {
        if ( userInformation == null )
        {
            return null;
        }
        return userInformation.getName ();
    }

    private EventBuilder createEvent ( final Date timestamp, final UserInformation userInformation, final String eventType, final Variant value )
    {
        final EventBuilder builder = createEventBuilder ();

        builder.attributes ( this.eventInformationAttributes );

        final Date now = new Date ();
        builder.entryTimestamp ( now );
        if ( timestamp != null )
        {
            builder.sourceTimestamp ( timestamp );
        }
        else
        {
            builder.sourceTimestamp ( now );
        }

        if ( userInformation != null && userInformation.getName () != null )
        {
            builder.attribute ( Fields.ACTOR_NAME, userInformation.getName () );
            builder.attribute ( Fields.ACTOR_TYPE, "USER" );
        }
        if ( value != null )
        {
            builder.attribute ( Fields.VALUE, value );
        }

        builder.attribute ( Fields.EVENT_TYPE, eventType );

        return builder;
    }

    protected boolean isActivated ()
    {
        return this.initialInformation != null;
    }

    private synchronized void applyState ( final StateInformation information )
    {
        final ConditionStatusInformation csi;

        logger.debug ( "Apply new state: {} for {}", new Object[] { information, getId () } );

        this.information = information;

        if ( isActivated () )
        {
            // if we are initialized we send out our current status
            this.initSent = false;
            final MapBuilder<String, Variant> mapBuilder = new MapBuilder<String, Variant> ();
            buildMonitorAttributes ( mapBuilder );
            csi = new ConditionStatusInformation ( getId (), generateStatus ( information ), information.getTimestamp (), information.getValue (), information.getLastAckTimestamp (), information.getLastAckUser (), mapBuilder.getMap () );
            storeData ( this.information );
        }
        else
        {
            logger.debug ( "Skipping apply notification since we are still un-initialized" );

            // otherwise send out our dummy status until we got initialized
            if ( !this.initSent )
            {
                csi = new ConditionStatusInformation ( getId (), ConditionStatus.INIT, new Date (), Variant.NULL, null, null, null );
                this.initSent = true;
            }
            else
            {
                csi = null;
            }
        }

        if ( csi != null )
        {
            notifyStateChange ( csi );
        }

    }

    protected void buildMonitorAttributes ( final MapBuilder<String, Variant> builder )
    {
        builder.putAll ( this.eventInformationAttributes );
    }

    private synchronized void applyAndSendStatus ( final StateInformation newInformation )
    {
        final ConditionStatusInformation oldConditionState = this.currentState;
        applyState ( newInformation );
        final ConditionStatusInformation newConditionState = this.currentState;
        sendStatusWhenChanged ( oldConditionState, newConditionState );
    }

    private synchronized void sendStatusWhenChanged ( final ConditionStatusInformation oldConditionState, final ConditionStatusInformation newConditionState )
    {
        final ConditionStatus oldState = oldConditionState.getStatus ();
        final ConditionStatus newState = newConditionState.getStatus ();

        if ( oldConditionState != newConditionState && oldState != ConditionStatus.INIT && newState != ConditionStatus.INIT )
        {
            publishEvent ( createEvent ( newConditionState.getStatusTimestamp (), null, newConditionState.getStatus ().toString (), newConditionState.getValue () ) );
        }
    }

    private static ConditionStatus generateStatus ( final StateInformation information )
    {
        if ( information.getActive () == null || information.getRequireAck () == null )
        {
            return ConditionStatus.INIT;
        }
        else if ( !information.getActive () )
        {
            return ConditionStatus.INACTIVE;
        }
        else if ( information.getValue () == null || information.getState () == State.UNSAFE )
        {
            return ConditionStatus.UNSAFE;
        }

        if ( !information.getRequireAck () )
        {
            return information.getState () == State.OK ? ConditionStatus.OK : ConditionStatus.NOT_OK;
        }
        else
        {
            final boolean isAckPending = ackPending ( information );
            if ( information.getState () == State.OK )
            {
                return isAckPending ? ConditionStatus.NOT_AKN : ConditionStatus.OK;
            }
            else
            {
                return isAckPending ? ConditionStatus.NOT_OK_NOT_AKN : ConditionStatus.NOT_OK_AKN;
            }
        }
    }

    private static boolean ackPending ( final StateInformation information )
    {
        if ( information.getLastFailTimestamp () == null )
        {
            return false;
        }
        if ( information.getLastAckTimestamp () == null )
        {
            return true;
        }
        return information.getLastFailTimestamp ().after ( information.getLastAckTimestamp () );
    }

    protected synchronized void publishEvent ( final EventBuilder builder )
    {
        if ( isActivated () )
        {
            this.eventProcessor.publishEvent ( builder.build () );
        }
    }

    protected void injectEventAttributes ( final EventBuilder builder )
    {
        builder.attributes ( this.eventInformationAttributes );

        builder.attribute ( Fields.SOURCE, getId () );
    }

}
