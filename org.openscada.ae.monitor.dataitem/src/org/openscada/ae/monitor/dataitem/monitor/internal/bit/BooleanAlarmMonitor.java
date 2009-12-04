package org.openscada.ae.monitor.dataitem.monitor.internal.bit;

import java.util.Map;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.dataitem.AbstractBooleanMonitor;
import org.openscada.ae.monitor.dataitem.DataItemMonitor;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.osgi.framework.BundleContext;

public class BooleanAlarmMonitor extends AbstractBooleanMonitor implements DataItemMonitor
{

    private boolean reference;

    public BooleanAlarmMonitor ( final BundleContext context, final EventProcessor eventProcessor, final String id )
    {
        super ( context, eventProcessor, id, "ae.monitor.booleanAlarm" );
    }

    @Override
    public void update ( final Map<String, String> properties ) throws Exception
    {
        super.update ( properties );

        this.reference = Boolean.parseBoolean ( properties.get ( "reference" ) );

        update ();
    }

    @Override
    protected void injectAttributes ( final Builder builder )
    {
        super.injectAttributes ( builder );
        builder.setAttribute ( this.prefix + ".reference", new Variant ( this.reference ) );
    }

    @Override
    protected void handleConfigUpdate ( final Map<String, String> configUpdate, final Map<String, Variant> attributes, final WriteAttributeResults result )
    {
        super.handleConfigUpdate ( configUpdate, attributes, result );
        final Variant reference = attributes.get ( this.prefix + ".reference" );
        if ( reference != null )
        {
            configUpdate.put ( "reference", reference.asBoolean () ? "true" : "false" );
            result.put ( this.prefix + ".reference", new WriteAttributeResult () );
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
