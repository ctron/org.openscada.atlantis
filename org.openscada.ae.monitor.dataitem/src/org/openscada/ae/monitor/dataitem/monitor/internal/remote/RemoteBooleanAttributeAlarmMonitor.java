package org.openscada.ae.monitor.dataitem.monitor.internal.remote;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.ae.ConditionStatus;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.ConditionListener;
import org.openscada.ae.monitor.MonitorService;
import org.openscada.ae.monitor.dataitem.DataItemMonitor;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.master.AbstractMasterHandlerImpl;
import org.openscada.da.master.MasterItem;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteBooleanAttributeAlarmMonitor extends AbstractMasterHandlerImpl implements DataItemMonitor, MonitorService
{

    private final static Logger logger = LoggerFactory.getLogger ( RemoteBooleanAttributeAlarmMonitor.class );

    public static final String FACTORY_ID = "ae.monitor.da.remote.booleanAttributeAlarm";

    private String attributeValue;

    private String attributeAck;

    private final String id;

    private ConditionStatus state;

    private Date timestamp;

    private final Set<ConditionListener> listeners = new HashSet<ConditionListener> ();

    private final Executor executor;

    private String attributeActive;

    public RemoteBooleanAttributeAlarmMonitor ( final Executor executor, final ObjectPoolTracker poolTracker, final EventProcessor eventProcessor, final String id, final int priority )
    {
        super ( poolTracker, priority );
        this.executor = executor;
        this.id = id;
    }

    public void init ()
    {
        setState ( ConditionStatus.UNSAFE );
    }

    @Override
    public synchronized DataItemValue dataUpdate ( final DataItemValue value )
    {
        if ( value == null )
        {
            setState ( ConditionStatus.UNSAFE );
            return null;
        }

        return handleUpdate ( value );
    }

    protected void setState ( final ConditionStatus state )
    {
        setState ( state, Calendar.getInstance () );
    }

    protected void setState ( final ConditionStatus state, final Calendar timestamp )
    {
        if ( this.state != state )
        {
            this.state = state;
            this.timestamp = timestamp.getTime ();
            logger.debug ( "State is: {}", state );

            final ConditionStatusInformation info = createStatus ();

            final ArrayList<ConditionListener> listnersClone = new ArrayList<ConditionListener> ( this.listeners );
            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    for ( final ConditionListener listener : listnersClone )
                    {
                        listener.statusChanged ( info );
                    }
                }
            } );
        }
    }

    protected DataItemValue handleUpdate ( final DataItemValue itemValue )
    {
        final Builder builder = new Builder ( itemValue );

        final Variant value = builder.getAttributes ().get ( this.attributeValue );
        final Variant ack = builder.getAttributes ().get ( this.attributeAck );
        final Variant active = builder.getAttributes ().get ( this.attributeActive );
        Calendar timestamp = itemValue.getTimestamp ();
        if ( timestamp == null )
        {
            timestamp = Calendar.getInstance ();
        }

        if ( value == null )
        {
            setState ( ConditionStatus.UNSAFE );
            return injectState ( builder ).build ();
        }

        final boolean alarmFlag = value.asBoolean ();

        final boolean activeFlag;
        if ( active == null )
        {
            activeFlag = true;
        }
        else
        {
            activeFlag = active.asBoolean ();
        }

        final ConditionStatus state;

        if ( !activeFlag )
        {
            state = ConditionStatus.INACTIVE;
        }
        else if ( ack == null )
        {
            state = alarmFlag ? ConditionStatus.NOT_OK : ConditionStatus.OK;
        }
        else
        {
            final boolean ackRequiredFlag = ack.asBoolean ();
            if ( alarmFlag )
            {
                if ( ackRequiredFlag )
                {
                    state = ConditionStatus.NOT_OK_NOT_AKN;
                }
                else
                {
                    state = ConditionStatus.NOT_OK_AKN;
                }
            }
            else
            {
                if ( ackRequiredFlag )
                {
                    state = ConditionStatus.NOT_AKN;
                }
                else
                {
                    state = ConditionStatus.OK;
                }
            }
        }

        setState ( state, timestamp );

        return injectState ( builder ).build ();
    }

    private Builder injectState ( final Builder builder )
    {
        builder.setAttribute ( this.id + ".state", new Variant ( this.state.toString () ) );
        return builder;
    }

    public synchronized void addStatusListener ( final ConditionListener listener )
    {
        if ( this.listeners.add ( listener ) )
        {
            final ConditionStatusInformation state = createStatus ();
            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    listener.statusChanged ( state );
                }
            } );
        }
    }

    private ConditionStatusInformation createStatus ()
    {
        return new ConditionStatusInformation ( this.id, this.state, this.timestamp, null, null, null );
    }

    public synchronized void removeStatusListener ( final ConditionListener listener )
    {
        this.listeners.remove ( listener );
    }

    public void akn ( final String aknUser, final Date aknTimestamp )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( this.attributeAck, Variant.TRUE );

        for ( final MasterItem item : getMasterItems () )
        {
            item.startWriteAttributes ( attributes );
        }
    }

    public String getId ()
    {
        return this.id;
    }

    public void setActive ( final boolean state )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( this.attributeActive, state ? Variant.TRUE : Variant.FALSE );

        for ( final MasterItem item : getMasterItems () )
        {
            item.startWriteAttributes ( attributes );
        }
    }

    @Override
    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        logger.debug ( "Apply update: {}", parameters );

        super.update ( parameters );
        this.attributeValue = parameters.get ( "attribute.value.name" );
        this.attributeAck = parameters.get ( "attribute.ack.name" );
        this.attributeActive = parameters.get ( "attribute.active.name" );

        reprocess ();

        logger.debug ( "Done applying" );
    }

    private void reprocess ()
    {
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                for ( final MasterItem item : getMasterItems () )
                {
                    item.reprocess ();
                }
            }
        } );
    }

}
