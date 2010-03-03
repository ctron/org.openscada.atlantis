package org.openscada.ae.monitor.common;

import java.util.Date;
import java.util.concurrent.Executor;

import org.openscada.ae.ConditionStatus;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.common.StateInformation.State;
import org.openscada.core.Variant;
import org.openscada.sec.UserInformation;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractStateMachineMonitorService extends AbstractPersistentMonitorService
{
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

    public AbstractStateMachineMonitorService ( final BundleContext context, final Executor executor, final EventProcessor eventProcessor, final String id )
    {
        super ( id, executor, context );
        this.eventProcessor = eventProcessor;
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
            final StateInformation newInformation = defaultState.apply ( this.initialInformation ).apply ( this.information );

            this.information = newInformation;

            // re-apply the current state
            applyState ( this.information );
        }
    }

    public void init ()
    {
        super.init ();
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
        applyState ( newInformation );
    }

    protected synchronized void setOk ( final Variant value, final Date timestamp )
    {
        if ( this.information.getState () != null && this.information.getState () == State.OK )
        {
            // no change
            return;
        }

        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setState ( State.OK );
        newInformation.setValue ( value );
        newInformation.setTimestamp ( timestamp );
        applyState ( newInformation );
    }

    protected synchronized void setUnsafe ()
    {
        if ( this.information.getState () != null && this.information.getState () == State.UNSAFE )
        {
            // no change
            return;
        }

        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setState ( State.UNSAFE );
        newInformation.setValue ( null );
        newInformation.setTimestamp ( new Date () );
        applyState ( newInformation );
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
        applyState ( newInformation );
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
        applyState ( newInformation );
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
        applyState ( newInformation );
    }

    private String getUserName ( final UserInformation userInformation )
    {
        if ( userInformation == null )
        {
            return null;
        }
        return userInformation.getName ();
    }

    private synchronized void applyState ( final StateInformation information )
    {
        final ConditionStatusInformation csi;

        logger.debug ( "Apply new state: {} for {}", new Object[] { information, getId () } );

        this.information = information;

        if ( this.initialInformation != null )
        {
            // if we are initialized we send out our current status
            this.initSent = false;
            csi = new ConditionStatusInformation ( getId (), generateStatus ( information ), information.getTimestamp (), information.getValue (), information.getLastAckTimestamp (), information.getLastAckUser () );
            storeData ( this.information );
        }
        else
        {
            logger.debug ( "Skipping apply notification since we are still un-initialized" );

            // otherwise send out our dummy status until we got initialized
            if ( !this.initSent )
            {
                csi = new ConditionStatusInformation ( getId (), ConditionStatus.INIT, new Date (), Variant.NULL, null, null );
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
        injectEventAttributes ( builder );
        this.eventProcessor.publishEvent ( builder.build () );
    }

    protected void injectEventAttributes ( final EventBuilder builder )
    {
    }

}
