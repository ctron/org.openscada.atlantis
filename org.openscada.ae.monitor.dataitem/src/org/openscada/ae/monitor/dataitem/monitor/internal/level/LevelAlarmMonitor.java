package org.openscada.ae.monitor.dataitem.monitor.internal.level;

import java.util.Date;
import java.util.Map;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.common.EventHelper;
import org.openscada.ae.monitor.dataitem.AbstractNumericMonitor;
import org.openscada.ae.monitor.dataitem.DataItemMonitor;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LevelAlarmMonitor extends AbstractNumericMonitor implements DataItemMonitor
{

    private final static Logger logger = LoggerFactory.getLogger ( LevelAlarmMonitor.class );

    private Double limit;

    private final boolean lowerOk;

    private final int priority;

    private final boolean cap;

    private boolean failure;

    private final boolean includedOk;

    public LevelAlarmMonitor ( final ObjectPoolTracker poolTracker, final EventProcessor eventProcessor, final String id, final String prefix, final String defaultMonitorType, final boolean lowerOk, final boolean includedOk, final int priority, final boolean cap )
    {
        super ( poolTracker, eventProcessor, id, prefix, defaultMonitorType );
        this.lowerOk = lowerOk;
        this.includedOk = includedOk;
        this.priority = priority;
        this.cap = cap;
    }

    @Override
    protected String getFactoryId ()
    {
        return this.prefix;
    }

    @Override
    protected String getConfigurationId ()
    {
        return getId ();
    }

    @Override
    public synchronized void update ( final Map<String, String> properties ) throws Exception
    {
        super.update ( properties );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( properties );

        final Double newLimit = cfg.getDouble ( "preset" );

        logger.debug ( "New limit: {}", newLimit );

        if ( newLimit == null )
        {
            setActive ( false );
        }

        if ( this.limit != newLimit )
        {
            this.limit = newLimit;
            publishEvent ( EventHelper.newConfigurationEvent ( this.getId (), "Change preset", new Variant ( newLimit ), new Date () ) );
        }

        reprocess ();
    }

    @Override
    protected boolean isError ()
    {
        return this.cap;
    }

    @Override
    protected void injectAttributes ( final Builder builder )
    {
        super.injectAttributes ( builder );

        final boolean active = isActive ();

        if ( active )
        {
            builder.setAttribute ( this.prefix + ".preset", new Variant ( this.limit ) );
        }
        builder.setAttribute ( this.prefix + ".active", active ? Variant.TRUE : Variant.FALSE );

        if ( this.cap && this.failure )
        {
            builder.setAttribute ( this.prefix + ".original.value", new Variant ( this.value ) );
        }
    }

    @Override
    protected void handleConfigUpdate ( final Map<String, String> configUpdate, final Map<String, Variant> attributes, final WriteAttributeResults result )
    {
        super.handleConfigUpdate ( configUpdate, attributes, result );

        final Variant active = attributes.get ( this.prefix + ".active" );
        if ( active != null )
        {
            configUpdate.put ( "active", "" + active.asBoolean () );
        }

        final Variant preset = attributes.get ( this.prefix + ".preset" );
        if ( preset != null )
        {
            if ( preset.isNull () )
            {
                configUpdate.put ( "active", "" + false );
            }
            else
            {
                configUpdate.put ( "preset", "" + preset.asDouble ( 0.0 ) );
            }
            result.put ( this.prefix + ".preset", WriteAttributeResult.OK );
        }

    }

    @Override
    protected int getDefaultPriority ()
    {
        return this.priority;
    }

    @Override
    protected synchronized void update ( final Builder builder )
    {
        logger.debug ( "Handle data update: {}", builder );

        if ( this.value == null || this.timestamp == null || this.limit == null )
        {
            setUnsafe ();
            return;
        }
        else if ( LevelHelper.isFailure ( this.value.doubleValue (), this.limit, this.lowerOk, this.includedOk ) )
        {
            this.failure = false;
            setOk ( new Variant ( this.value ), this.timestamp );
        }
        else
        {
            this.failure = true;
            if ( this.cap && isActive () )
            {
                builder.setValue ( new Variant ( this.limit ) );
            }
            setFailure ( new Variant ( this.value ), this.timestamp );
        }
    }

}
