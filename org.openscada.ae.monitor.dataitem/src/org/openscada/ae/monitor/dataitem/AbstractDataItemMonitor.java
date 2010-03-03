package org.openscada.ae.monitor.dataitem;

import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ae.ConditionStatus;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.common.AbstractStateMachineMonitorService;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.master.MasterItem;
import org.openscada.da.master.MasterItemHandler;
import org.openscada.da.master.WriteRequest;
import org.openscada.da.master.WriteRequestResult;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.openscada.utils.osgi.pool.SingleObjectPoolServiceTracker;
import org.openscada.utils.osgi.pool.SingleObjectPoolServiceTracker.ServiceListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDataItemMonitor extends AbstractStateMachineMonitorService implements DataItemMonitor
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractDataItemMonitor.class );

    private String masterId;

    private SingleObjectPoolServiceTracker tracker;

    protected MasterItem masterItem;

    private MasterItemHandler handler;

    protected final String prefix;

    private boolean requireAkn = false;

    private boolean active = true;

    private boolean akn;

    private ConditionStatus state;

    private int handlerPriority;

    private boolean alarm;

    private boolean unsafe;

    private final String defaultMonitorType;

    private String monitorType;

    private String component;

    private String message;

    private String messageCode;

    private final ObjectPoolTracker poolTracker;

    protected Map<String, Variant> attributes = new HashMap<String, Variant> ();

    public AbstractDataItemMonitor ( final BundleContext context, final Executor executor, final ObjectPoolTracker poolTracker, final EventProcessor eventProcessor, final String id, final String prefix, final String defaultMonitorType )
    {
        super ( context, executor, eventProcessor, id );
        this.poolTracker = poolTracker;
        this.prefix = prefix;
        this.defaultMonitorType = defaultMonitorType;
    }

    public void dispose ()
    {
        disconnect ();
    }

    protected static Map<String, Variant> convertAttributes ( final Map<String, String> parameters )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        for ( final Map.Entry<String, String> entry : parameters.entrySet () )
        {
            final String key = entry.getKey ();
            if ( key.startsWith ( "info." ) )
            {
                attributes.put ( key.substring ( "info.".length () ), new Variant ( entry.getValue () ) );
            }
        }

        return attributes;
    }

    public synchronized void update ( final Map<String, String> properties ) throws Exception
    {
        disconnect ();

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( properties );

        this.masterId = cfg.getStringChecked ( MasterItem.MASTER_ID, "'" + MasterItem.MASTER_ID + "' must be set" );
        this.handlerPriority = cfg.getInteger ( "handlerPriority", getDefaultPriority () );
        this.monitorType = cfg.getString ( "monitorType", this.defaultMonitorType );

        this.attributes = convertAttributes ( properties );

        init ();

        setActive ( cfg.getBoolean ( "active", true ) );
        setRequireAkn ( cfg.getBoolean ( "requireAck", false ) );
        connect ();
    }

    protected int getDefaultPriority ()
    {
        return 0;
    }

    private synchronized void connect () throws InvalidSyntaxException
    {
        if ( this.masterId == null )
        {
            setUnsafe ();
            throw new RuntimeException ( String.format ( "'%s' is not set", MasterItem.MASTER_ID ) );
        }

        logger.debug ( "Setting up for master item: {}", this.masterId );

        this.tracker = new SingleObjectPoolServiceTracker ( this.poolTracker, this.masterId, new ServiceListener () {

            public void serviceChange ( final Object service, final Dictionary<?, ?> properties )
            {
                AbstractDataItemMonitor.this.setMasterItem ( (MasterItem)service );
            }
        } );

        this.tracker.open ();
    }

    protected void setMasterItem ( final MasterItem masterItem )
    {
        logger.info ( "Setting master item:{}", masterItem );

        disconnectItem ();
        connectItem ( masterItem );
    }

    private synchronized void connectItem ( final MasterItem masterItem )
    {
        logger.debug ( "Connecting to master item: {}", masterItem );

        this.masterItem = masterItem;
        if ( this.masterItem != null )
        {
            this.masterItem.addHandler ( this.handler = new MasterItemHandler () {

                public WriteRequestResult processWrite ( final WriteRequest request )
                {
                    return AbstractDataItemMonitor.this.handleProcessWrite ( request );
                }

                public DataItemValue dataUpdate ( final Map<String, Object> context, final DataItemValue value )
                {
                    logger.debug ( "Handle data update: {}", value );
                    return AbstractDataItemMonitor.this.handleDataUpdate ( value );
                }
            }, this.handlerPriority );
        }
    }

    private synchronized void disconnectItem ()
    {
        logger.debug ( "Disconnect from master item: {}", this.masterItem );

        if ( this.masterItem != null )
        {
            this.masterItem.removeHandler ( this.handler );
            this.masterItem = null;
            this.handler = null;
        }
    }

    private synchronized void disconnect ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
            this.tracker = null;
        }
    }

    private DataItemValue handleDataUpdate ( final DataItemValue value )
    {
        final DataItemValue.Builder builder = new DataItemValue.Builder ( value );

        performDataUpdate ( builder );
        injectAttributes ( builder );

        final DataItemValue newValue = builder.build ();
        logger.info ( "Setting new value: {}", newValue );

        return newValue;
    }

    protected abstract void performDataUpdate ( Builder builder );

    /**
     * Return the factory id that configured this instance
     * @return the factory id
     */
    protected abstract String getFactoryId ();

    /**
     * Return the configuration id that is assigned to this instance
     * @return the configuration id
     */
    protected abstract String getConfigurationId ();

    @Override
    protected void notifyStateChange ( final ConditionStatusInformation status )
    {
        super.notifyStateChange ( status );
        this.state = status.getStatus ();
        this.akn = this.state == ConditionStatus.NOT_AKN || this.state == ConditionStatus.NOT_OK_NOT_AKN;
        this.unsafe = this.state == ConditionStatus.UNSAFE;
        this.alarm = this.state == ConditionStatus.NOT_OK || this.state == ConditionStatus.NOT_OK_AKN || this.state == ConditionStatus.NOT_OK_NOT_AKN;
        reprocess ();
    }

    protected boolean isError ()
    {
        return false;
    }

    protected boolean isActive ()
    {
        return this.active;
    }

    /**
     * Inject attributes to the value after the value update has been performed using
     * {@link #performDataUpdate(Builder)}
     * @param builder the builder to use for changing information
     */
    protected void injectAttributes ( final Builder builder )
    {
        builder.setAttribute ( this.prefix + ".active", new Variant ( this.active ) );
        builder.setAttribute ( this.prefix + ".requireAck", new Variant ( this.requireAkn ) );

        builder.setAttribute ( this.prefix + ".ackRequired", this.akn ? Variant.TRUE : Variant.FALSE );
        builder.setAttribute ( this.prefix + ".state", new Variant ( this.state.toString () ) );

        builder.setAttribute ( this.prefix + ".unsafe", this.unsafe ? Variant.TRUE : Variant.FALSE );

        if ( isError () )
        {
            builder.setAttribute ( this.prefix + ".error", this.alarm ? Variant.TRUE : Variant.FALSE );
        }
        else
        {
            builder.setAttribute ( this.prefix + ".alarm", this.alarm ? Variant.TRUE : Variant.FALSE );
        }
    }

    protected WriteRequestResult handleProcessWrite ( final WriteRequest request )
    {
        if ( request.getAttributes () != null )
        {
            return handleAttributesWrite ( request );
        }
        return null;
    }

    protected WriteRequestResult handleAttributesWrite ( final WriteRequest request )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ( request.getAttributes () );
        final WriteAttributeResults result = new WriteAttributeResults ();

        simpleHandleAttributes ( attributes, result );

        // remove result keys from request
        for ( final String attr : result.keySet () )
        {
            attributes.remove ( attr );
        }

        return new WriteRequestResult ( request.getValue (), attributes, result );
    }

    protected void simpleHandleAttributes ( final Map<String, Variant> attributes, final WriteAttributeResults result )
    {
        final Map<String, String> configUpdate = new HashMap<String, String> ();

        handleConfigUpdate ( configUpdate, attributes, result );

        if ( !configUpdate.isEmpty () )
        {
            updateConfiguration ( configUpdate );
        }
    }

    private void updateConfiguration ( final Map<String, String> configUpdate )
    {
        logger.info ( "Request to update configuration: {}", configUpdate );

        final String factoryId = getFactoryId ();
        final String configurationId = getConfigurationId ();

        logger.info ( "Directing update to: {}/{}", new Object[] { factoryId, configurationId } );

        if ( factoryId != null && configurationId != null )
        {
            try
            {
                Activator.getConfigAdmin ().updateConfiguration ( factoryId, configurationId, configUpdate, false );
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed to update configuration", e );
                throw new RuntimeException ( "Unable to update configuration", e );
            }
        }
    }

    @Override
    public synchronized void setRequireAkn ( final boolean state )
    {
        super.setRequireAkn ( state );
        this.requireAkn = state;
        reprocess ();
    }

    @Override
    public synchronized void setActive ( final boolean state )
    {
        super.setActive ( state );
        this.active = state;
        reprocess ();
    }

    protected void reprocess ()
    {
        final MasterItem item = this.masterItem;
        if ( item != null )
        {
            item.reprocess ();
        }
    }

    protected void handleConfigUpdate ( final Map<String, String> configUpdate, final Map<String, Variant> attributes, final WriteAttributeResults result )
    {
        final Variant active = attributes.get ( this.prefix + ".active" );
        if ( active != null )
        {
            configUpdate.put ( "active", active.asBoolean () ? "true" : "false" );
            result.put ( this.prefix + ".active", WriteAttributeResult.OK );
        }

        final Variant requireAkn = attributes.get ( this.prefix + ".requireAck" );
        if ( requireAkn != null )
        {
            configUpdate.put ( "requireAck", requireAkn.asBoolean () ? "true" : "false" );
            result.put ( this.prefix + ".requireAck", WriteAttributeResult.OK );
        }
    }

    protected static Date toTimestamp ( final DataItemValue value )
    {
        if ( value == null )
        {
            return new Date ();
        }
        final Calendar c = value.getTimestamp ();
        if ( c == null )
        {
            return new Date ();
        }
        else
        {
            return c.getTime ();
        }
    }

    @Override
    protected void injectEventAttributes ( final EventBuilder builder )
    {
        super.injectEventAttributes ( builder );
        builder.attribute ( Event.Fields.MONITOR_TYPE, this.monitorType );
        if ( this.component != null )
        {
            builder.attribute ( Event.Fields.COMPONENT, this.component );
        }
        if ( this.message != null )
        {
            builder.attribute ( Event.Fields.MESSAGE, this.message );
        }
        if ( this.messageCode != null )
        {
            builder.attribute ( Event.Fields.MESSAGE_CODE, this.messageCode );
        }
    }

}