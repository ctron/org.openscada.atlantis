package org.openscada.ae.monitor.common;

import java.util.Date;
import java.util.concurrent.Executor;

import org.openscada.ae.ConditionStatus;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.event.EventProcessor;
import org.openscada.core.Variant;
import org.openscada.sec.UserInformation;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractStateMachineMonitorService extends AbstractPersistentMonitorService
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractMonitorService.class );

    private final EventProcessor eventProcessor;

    private StateInformation information = new StateInformation ();

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
        logger.debug ( "Setting persistens state: {}", state );
    }

    public void init ()
    {
        // FIXME: remove when we are done
    }

    @Override
    protected void switchToInit ()
    {
        // FIXME: implement
    }

    protected synchronized void setFailure ( final Variant value, final Date timestamp )
    {
        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setOk ( false );
        newInformation.setValue ( value );
        newInformation.setTimestamp ( timestamp );
        newInformation.setLastFailTimestamp ( timestamp );
        applyState ( newInformation );
    }

    protected synchronized void setOk ( final Variant value, final Date timestamp )
    {
        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setOk ( true );
        newInformation.setValue ( value );
        newInformation.setTimestamp ( timestamp );
        applyState ( newInformation );
    }

    protected synchronized void setUnsafe ()
    {
        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setOk ( true );
        newInformation.setValue ( null );
        newInformation.setTimestamp ( new Date () );
        applyState ( newInformation );
    }

    public synchronized void setActive ( final boolean state )
    {
        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setActive ( state );
        applyState ( newInformation );
    }

    public synchronized void akn ( final UserInformation userInformation, final Date aknTimestamp )
    {
        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setLastAckTimestamp ( aknTimestamp );
        newInformation.setLastAckUser ( getUserName ( userInformation ) );
        applyState ( newInformation );
    }

    public synchronized void setRequireAkn ( final boolean state )
    {
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
        this.information = information;
        final ConditionStatusInformation csi = new ConditionStatusInformation ( getId (), generateStatus ( information ), information.getTimestamp (), information.getValue (), information.getLastAckTimestamp (), information.getLastAckUser () );
        notifyStateChange ( csi );
    }

    private static ConditionStatus generateStatus ( final StateInformation information )
    {
        if ( information.getActive () == null || information.getRequireAck () == null || information.getOk () == null )
        {
            return ConditionStatus.INIT;
        }
        else if ( !information.getActive () )
        {
            return ConditionStatus.INACTIVE;
        }
        else if ( information.getValue () == null )
        {
            return ConditionStatus.UNSAFE;
        }

        if ( !information.getRequireAck () )
        {
            return information.getOk () ? ConditionStatus.OK : ConditionStatus.NOT_OK;
        }
        else
        {
            final boolean isAckPending = ackPending ( information );
            if ( information.getOk () )
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
