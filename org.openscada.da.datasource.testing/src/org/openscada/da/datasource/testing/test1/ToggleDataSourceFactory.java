package org.openscada.da.datasource.testing.test1;

import java.util.concurrent.ScheduledExecutorService;

import org.openscada.da.datasource.testing.AbstractDataSourceFactory;
import org.openscada.da.datasource.testing.DefaultDataSource;
import org.osgi.framework.BundleContext;

public class ToggleDataSourceFactory extends AbstractDataSourceFactory
{
    public ToggleDataSourceFactory ( final BundleContext context, final ScheduledExecutorService scheduler )
    {
        super ( context, scheduler );
    }

    @Override
    protected DefaultDataSource createDataSource ()
    {
        return new SineDataSource ( getScheduler () );
    }

}
