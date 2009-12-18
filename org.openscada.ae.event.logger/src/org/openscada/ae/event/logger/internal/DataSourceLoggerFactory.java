package org.openscada.ae.event.logger.internal;

import java.util.Map;

import org.openscada.da.master.MasterItem;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

public class DataSourceLoggerFactory extends AbstractServiceConfigurationFactory<MasterItemLogger>
{

    private final ObjectPoolTracker poolTracker;

    public DataSourceLoggerFactory ( final BundleContext context ) throws InvalidSyntaxException
    {
        super ( context );
        this.poolTracker = new ObjectPoolTracker ( context, MasterItem.class.getName () );
        this.poolTracker.open ();
    }

    @Override
    public synchronized void dispose ()
    {
        this.poolTracker.close ();
        super.dispose ();
    }

    @Override
    protected Entry<MasterItemLogger> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final MasterItemLogger logger = new MasterItemLogger ( context, this.poolTracker, 0 );
        logger.update ( parameters );
        return new Entry<MasterItemLogger> ( configurationId, logger );
    }

    @Override
    protected void disposeService ( final String id, final MasterItemLogger service )
    {
        service.dispose ();
    }

    @Override
    protected Entry<MasterItemLogger> updateService ( final String configurationId, final Entry<MasterItemLogger> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

}
