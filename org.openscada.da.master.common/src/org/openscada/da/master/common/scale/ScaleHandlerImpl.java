package org.openscada.da.master.common.scale;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.da.master.AbstractConfigurableMasterHandlerImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.util.tracker.ServiceTracker;

public class ScaleHandlerImpl extends AbstractConfigurableMasterHandlerImpl
{
    private boolean active = false;

    private double scale = 1.0;

    private double offset = 0.0;

    public ScaleHandlerImpl ( final String configurationId, final ObjectPoolTracker poolTracker, final int priority, final ServiceTracker caTracker )
    {
        super ( configurationId, poolTracker, priority, caTracker, ScaleHandlerFactoryImpl.FACTORY_ID, ScaleHandlerFactoryImpl.FACTORY_ID );
    }

    @Override
    public DataItemValue dataUpdate ( final DataItemValue value )
    {
        if ( value == null )
        {
            return null;
        }

        return processDataUpdate ( value );
    }

    protected DataItemValue processDataUpdate ( final DataItemValue value )
    {
        final Builder builder = new Builder ( value );

        injectAttributes ( builder );
        builder.setAttribute ( ScaleHandlerFactoryImpl.FACTORY_ID + ".original.value", value.getValue () );

        final Variant val = value.getValue ();
        if ( val == null || val.isNull () )
        {
            return builder.build ();
        }

        builder.setValue ( handleDataUpdate ( builder.getValue () ) );
        return builder.build ();
    }

    private Variant handleDataUpdate ( final Variant value )
    {
        if ( !this.active )
        {
            return value;
        }
        else
        {
            return new Variant ( value.asDouble ( null ) * this.scale + this.offset );
        }
    }

    @Override
    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        super.update ( parameters );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        this.scale = cfg.getDouble ( "scale", 1 );
        this.offset = cfg.getDouble ( "offset", 0 );
        this.active = cfg.getBoolean ( "active", false );

        reprocess ();
    }

    protected void injectAttributes ( final Builder builder )
    {
        builder.setAttribute ( ScaleHandlerFactoryImpl.FACTORY_ID + ".active", this.active ? Variant.TRUE : Variant.FALSE );
        builder.setAttribute ( ScaleHandlerFactoryImpl.FACTORY_ID + ".scale", new Variant ( this.scale ) );
        builder.setAttribute ( ScaleHandlerFactoryImpl.FACTORY_ID + ".offset", new Variant ( this.offset ) );
    }

    @Override
    protected WriteAttributeResults handleUpdate ( final WriteInformation writeInformation, final Map<String, Variant> attributes ) throws Exception
    {
        final Map<String, String> data = new HashMap<String, String> ();

        final Variant active = attributes.get ( "active" );
        final Variant scale = attributes.get ( "scale" );
        final Variant offset = attributes.get ( "offset" );

        if ( active != null && !active.isNull () )
        {
            data.put ( "active", active.asString () );
        }
        if ( scale != null && !scale.isNull () )
        {
            data.put ( "scale", scale.asString () );
        }
        if ( offset != null && !offset.isNull () )
        {
            data.put ( "offset", offset.asString () );
        }

        return updateConfiguration ( data, false );
    }

}
