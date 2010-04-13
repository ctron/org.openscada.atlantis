package org.openscada.da.master.common;

import java.util.Date;
import java.util.Map;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.master.AbstractConfigurableMasterHandlerImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.util.tracker.ServiceTracker;

public abstract class AbstractCommonHandlerImpl extends AbstractConfigurableMasterHandlerImpl
{

    public AbstractCommonHandlerImpl ( final String configurationId, final ObjectPoolTracker poolTracker, final int priority, final ServiceTracker caTracker, final String prefix, final String factoryId )
    {
        super ( configurationId, poolTracker, priority, caTracker, prefix, factoryId );
    }

    protected abstract DataItemValue processDataUpdate ( final DataItemValue value ) throws Exception;

    /**
     * Create a pre-filled event builder
     * @return a new event builder
     */
    protected EventBuilder createEventBuilder ()
    {
        final EventBuilder builder = Event.create ();

        builder.sourceTimestamp ( new Date () );
        builder.entryTimestamp ( new Date () );

        builder.attributes ( this.eventAttributes );

        return builder;
    }

    @Override
    public DataItemValue dataUpdate ( final Map<String, Object> context, final DataItemValue value )
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

}