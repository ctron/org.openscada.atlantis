package org.openscada.ae.monitor.dataitem.monitor.internal.remote;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ae.ConditionStatus;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.MonitorService;
import org.openscada.ae.monitor.dataitem.DataItemMonitor;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.master.MasterItem;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteBooleanValueAlarmMonitor extends GenericRemoteMonitor implements DataItemMonitor, MonitorService
{

    final static Logger logger = LoggerFactory.getLogger ( RemoteBooleanValueAlarmMonitor.class );

    public static final String FACTORY_ID = "ae.monitor.da.remote.booleanValueAlarm";

    private String attributeAck;

    private String attributeActive;

    public RemoteBooleanValueAlarmMonitor ( final Executor executor, final ObjectPoolTracker poolTracker, final EventProcessor eventProcessor, final String id, final int priority )
    {
        super ( executor, poolTracker, priority, id, eventProcessor );
    }

    protected DataItemValue handleUpdate ( final DataItemValue itemValue )
    {
        logger.debug ( "Perform update" );

        final Builder builder = new Builder ( itemValue );

        final Variant value = itemValue.getValue ();
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

    public void akn ( final String aknUser, final Date aknTimestamp )
    {
        publishAckRequestEvent ();

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( this.attributeAck, Variant.TRUE );

        for ( final MasterItem item : getMasterItems () )
        {
            item.startWriteAttributes ( attributes );
        }
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
        this.attributeAck = parameters.get ( "attribute.ack.name" );
        this.attributeActive = parameters.get ( "attribute.active.name" );

        reprocess ();

        logger.debug ( "Done applying" );
    }

}
