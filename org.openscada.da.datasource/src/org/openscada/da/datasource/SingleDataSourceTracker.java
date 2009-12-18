package org.openscada.da.datasource;

import java.util.Dictionary;

import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.openscada.utils.osgi.pool.SingleObjectPoolServiceTracker;
import org.osgi.framework.InvalidSyntaxException;

public class SingleDataSourceTracker
{
    public interface ServiceListener
    {
        public void dataSourceChanged ( DataSource dataSource );
    }

    private final SingleObjectPoolServiceTracker tracker;

    private final ServiceListener listener;

    public SingleDataSourceTracker ( final ObjectPoolTracker poolTracker, final String dataSourceId, final ServiceListener listener ) throws InvalidSyntaxException
    {
        this.listener = listener;

        this.tracker = new SingleObjectPoolServiceTracker ( poolTracker, dataSourceId, new SingleObjectPoolServiceTracker.ServiceListener () {
            public void serviceChange ( final Object service, final Dictionary<?, ?> properties )
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
