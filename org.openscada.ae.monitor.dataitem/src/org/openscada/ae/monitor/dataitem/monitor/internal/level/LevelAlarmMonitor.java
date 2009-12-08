package org.openscada.ae.monitor.dataitem.monitor.internal.level;

import java.util.Date;
import java.util.Map;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.common.EventHelper;
import org.openscada.ae.monitor.dataitem.AbstractNumericMonitor;
import org.openscada.ae.monitor.dataitem.DataItemMonitor;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.osgi.framework.BundleContext;

public class LevelAlarmMonitor extends AbstractNumericMonitor implements DataItemMonitor
{

    private double limit;

    private final boolean lowerOk;

    private final int priority;

    private final boolean cap;

    public LevelAlarmMonitor ( final BundleContext context, final EventProcessor eventProcessor, final String id, final String prefix, final boolean lowerOk, final int priority, final boolean cap )
    {
        super ( context, eventProcessor, id, prefix );
        this.lowerOk = lowerOk;
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
    public void update ( final Map<String, String> properties ) throws Exception
    {
        super.update ( properties );

        final double newLimit = Double.parseDouble ( properties.get ( "preset" ) );

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

        builder.setAttribute ( this.prefix + ".preset", new Variant ( this.limit ) );
        if ( this.cap )
        {
            builder.setAttribute ( this.prefix + ".original.value", new Variant ( this.value ) );
        }
    }

    @Override
    protected void handleConfigUpdate ( final Map<String, String> configUpdate, final Map<String, Variant> attributes, final WriteAttributeResults result )
    {
        super.handleConfigUpdate ( configUpdate, attributes, result );
        final Variant preset = attributes.get ( this.prefix + ".preset" );
        if ( preset != null )
        {
            configUpdate.put ( "preset", "" + preset.asDouble ( 0.0 ) );
            result.put ( this.prefix + ".preset", new WriteAttributeResult () );
        }
    }

    @Override
    protected int getDefaultPriority ()
    {
        return this.priority;
    }

    @Override
    protected void update ( final Builder builder )
    {
        if ( this.value == null || this.timestamp == null )
        {
            setUnsafe ();
            return;
        }

        else if ( this.value.doubleValue () < this.limit && this.lowerOk || this.value.doubleValue () > this.limit && !this.lowerOk )
        {
            setOk ( new Variant ( this.value ), this.timestamp );
        }
        else
        {
            if ( this.cap )
            {
                builder.setValue ( new Variant ( this.limit ) );
            }
            setFailure ( new Variant ( this.value ), this.timestamp );
        }
    }
}
