package org.openscada.ae.event.logger.internal;

import java.util.Map;

import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.osgi.framework.BundleContext;

public class DataSourceLoggerFactory extends AbstractServiceConfigurationFactory<MasterItemLogger>
{

    public DataSourceLoggerFactory ( final BundleContext context )
    {
        super ( context );
    }

    @Override
    protected Entry<MasterItemLogger> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final MasterItemLogger logger = new MasterItemLogger ( context, 0 );
        logger.update ( parameters );
        return new Entry<MasterItemLogger> ( logger );
    }

    @Override
    protected void disposeService ( final MasterItemLogger service )
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
