package org.openscada.da.datasource;

import java.util.Set;

import org.openscada.utils.osgi.FilterUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class MultiDataSourceTracker
{
    private final ServiceTracker tracker;

    private final ServiceListener listener;

    public interface ServiceListener
    {
        public void dataSourceAdded ( ServiceReference reference, DataSource dataSource );

        public void dataSourceRemoved ( ServiceReference reference, DataSource dataSource );

        public void dataSourceModified ( ServiceReference reference, DataSource dataSource );
    }

    public MultiDataSourceTracker ( final BundleContext context, final Set<String> dataSourceIds, final ServiceListener listener ) throws InvalidSyntaxException
    {
        this.tracker = new ServiceTracker ( context, createFilter ( dataSourceIds ), new ServiceTrackerCustomizer () {

            public void removedService ( final ServiceReference reference, final Object service )
            {
                MultiDataSourceTracker.this.handleRemoved ( reference, (DataSource)service );
            }

            public void modifiedService ( final ServiceReference reference, final Object service )
            {
                MultiDataSourceTracker.this.handleModified ( reference, (DataSource)service );
            }

            public Object addingService ( final ServiceReference reference )
            {
                final Object o = context.getService ( reference );
                if ( o instanceof DataSource )
                {
                    final DataSource service = (DataSource)o;
                    try
                    {
                        MultiDataSourceTracker.this.handleAdded ( reference, service );
                        return service;
                    }
                    catch ( final Throwable e )
                    {
                        context.ungetService ( reference );
                        return null;
                    }
                }
                else
                {
                    context.ungetService ( reference );
                    return null;
                }

            }
        } );

        this.listener = listener;
    }

    protected void handleAdded ( final ServiceReference reference, final DataSource service )
    {
        this.listener.dataSourceAdded ( reference, service );
    }

    protected void handleModified ( final ServiceReference reference, final DataSource service )
    {
        this.listener.dataSourceModified ( reference, service );
    }

    protected void handleRemoved ( final ServiceReference reference, final DataSource service )
    {
        this.listener.dataSourceRemoved ( reference, service );
    }

    private static Filter createFilter ( final Set<String> sources ) throws InvalidSyntaxException
    {
        return FilterUtil.createAndFilter ( FilterUtil.createClassFilter ( DataSource.class.getName () ), FilterUtil.createSimpleOr ( DataSource.DATA_SOURCE_ID, sources ) );
    }

    public void open ()
    {
        this.tracker.open ();
    }

    public void close ()
    {
        this.tracker.close ();
    }
}
