package org.openscada.da.datasource;

import java.util.HashMap;
import java.util.Map;

import org.openscada.utils.osgi.FilterUtil;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class SingleDataSourceTracker
{
    public interface ServiceListener
    {
        public void dataSourceChanged ( DataSource dataSource );
    }

    private final SingleServiceTracker tracker;

    private final ServiceListener listener;

    public SingleDataSourceTracker ( final BundleContext context, final String dataSourceId, final ServiceListener listener ) throws InvalidSyntaxException
    {
        this.listener = listener;
        final Map<String, String> parameters = new HashMap<String, String> ();
        parameters.put ( DataSource.DATA_SOURCE_ID, dataSourceId );
        final Filter filter = FilterUtil.createAndFilter ( DataSource.class.getName (), parameters );
        this.tracker = new SingleServiceTracker ( context, filter, new SingleServiceListener () {

            public void serviceChange ( final ServiceReference reference, final Object service )
            {
                SingleDataSourceTracker.this.setDataSource ( (DataSource)service );
            }
        } );
    }

    protected void setDataSource ( final DataSource service )
    {
        this.listener.dataSourceChanged ( service );
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
