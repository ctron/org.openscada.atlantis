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

    private double factor = 1.0;

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

        try
        {
            return processDataUpdate ( value );
        }
        catch ( final Throwable e )
        {
            final Builder builder = new Builder ( value );
            builder.setAttribute ( getPrefixed ( "error" ), Variant.TRUE );
            builder.setAttribute ( getPrefixed ( "error.message" ), new Variant ( e.getMessage () ) );
            return builder.build ();
        }
    }

    protected DataItemValue processDataUpdate ( final DataItemValue value ) throws Exception
    {
        final Builder builder = new Builder ( value );

        injectAttributes ( builder );
        builder.setAttribute ( getPrefixed ( "raw" ), value.getValue () );

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
            return new Variant ( value.asDouble ( null ) * this.factor + this.offset );
        }
    }

    @Override
    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        super.update ( parameters );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        this.factor = cfg.getDouble ( "factor", 1 );
        this.offset = cfg.getDouble ( "offset", 0 );
        this.active = cfg.getBoolean ( "active", false );

        reprocess ();
    }

    protected void injectAttributes ( final Builder builder )
    {
        builder.setAttribute ( getPrefixed ( "active" ), this.active ? Variant.TRUE : Variant.FALSE );
        builder.setAttribute ( getPrefixed ( "factor" ), new Variant ( this.factor ) );
        builder.setAttribute ( getPrefixed ( "offset" ), new Variant ( this.offset ) );
    }

    @Override
    protected WriteAttributeResults handleUpdate ( final WriteInformation writeInformation, final Map<String, Variant> attributes ) throws Exception
    {
        final Map<String, String> data = new HashMap<String, String> ();

        final Variant active = attributes.get ( "active" );
        final Variant factor = attributes.get ( "factor" );
        final Variant offset = attributes.get ( "offset" );

        if ( active != null && !active.isNull () )
        {
            data.put ( "active", active.asString () );
        }
        if ( factor != null && !factor.isNull () )
        {
            data.put ( "factor", factor.asString () );
        }
        if ( offset != null && !offset.isNull () )
        {
            data.put ( "offset", offset.asString () );
        }

        return updateConfiguration ( data, false );
    }

}
