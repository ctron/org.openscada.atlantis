package org.openscada.da.master.common;

import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.master.AbstractConfigurableMasterHandlerImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.util.tracker.ServiceTracker;

public abstract class AbstractCommonHandlerImpl extends AbstractConfigurableMasterHandlerImpl
{

    public AbstractCommonHandlerImpl ( String configurationId, ObjectPoolTracker poolTracker, int priority, ServiceTracker caTracker, String prefix, String factoryId )
    {
        super ( configurationId, poolTracker, priority, caTracker, prefix, factoryId );
    }

    protected abstract DataItemValue processDataUpdate ( final DataItemValue value ) throws Exception;

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

}