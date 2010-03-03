package org.openscada.ae.monitor.dataitem.monitor.internal.bit;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.common.EventHelper;
import org.openscada.ae.monitor.dataitem.AbstractBooleanMonitor;
import org.openscada.ae.monitor.dataitem.DataItemMonitor;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;

public class BooleanAlarmMonitor extends AbstractBooleanMonitor implements DataItemMonitor
{

    public static final String FACTORY_ID = "ae.monitor.da.booleanAlarm";

    private boolean reference;

    public BooleanAlarmMonitor ( final BundleContext context, final Executor executor, final ObjectPoolTracker poolTracker, final EventProcessor eventProcessor, final String id )
    {
        super ( context, executor, poolTracker, eventProcessor, id, "ae.monitor.booleanAlarm", "VALUE" );
    }

    @Override
    protected String getFactoryId ()
    {
        return FACTORY_ID;
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

        final boolean newReference = Boolean.parseBoolean ( properties.get ( "reference" ) );
        if ( newReference != this.reference )
        {
            publishEvent ( EventHelper.newConfigurationEvent ( this.getId (), "Change reference", Variant.valueOf ( newReference ), new Date () ) );
            this.reference = newReference;
        }

        update ();
    }

    @Override
    protected void injectAttributes ( final Builder builder )
    {
        super.injectAttributes ( builder );
        builder.setAttribute ( this.prefix + ".reference", Variant.valueOf ( this.reference ) );
    }

    @Override
    protected void handleConfigUpdate ( final Map<String, String> configUpdate, final Map<String, Variant> attributes, final WriteAttributeResults result )
    {
        super.handleConfigUpdate ( configUpdate, attributes, result );
        final Variant reference = attributes.get ( this.prefix + ".reference" );
        if ( reference != null )
        {
            configUpdate.put ( "reference", reference.asBoolean () ? "true" : "false" );
            result.put ( this.prefix + ".reference", WriteAttributeResult.OK );
        }
    }

    @Override
    protected void update ()
    {
        if ( this.value == null || this.timestamp == null )
        {
            setUnsafe ();
        }
        else if ( this.value == this.reference )
        {
            setOk ( new Variant ( this.value ), this.timestamp );
        }
        else
        {
            setFailure ( new Variant ( this.value ), this.timestamp );
        }
    }
}
