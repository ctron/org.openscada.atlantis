package org.openscada.da.datasource;

import java.util.Collection;
import java.util.Dictionary;
import java.util.LinkedList;
import java.util.Set;

import org.openscada.utils.osgi.pool.ObjectPoolListener;
import org.openscada.utils.osgi.pool.ObjectPoolServiceTracker;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.InvalidSyntaxException;

public class MultiDataSourceTracker
{
    private final Collection<ObjectPoolServiceTracker> trackers;

    private final ServiceListener listener;

    public interface ServiceListener
    {
        public void dataSourceAdded ( String id, Dictionary<?, ?> properties, DataSource dataSource );

        public void dataSourceRemoved ( String id, Dictionary<?, ?> properties, DataSource dataSource );

        public void dataSourceModified ( String id, Dictionary<?, ?> properties, DataSource dataSource );
    }

    public MultiDataSourceTracker ( final ObjectPoolTracker poolTracker, final Set<String> dataSourceIds, final ServiceListener listener ) throws InvalidSyntaxException
    {
        this.trackers = new LinkedList<ObjectPoolServiceTracker> ();

        this.listener = listener;

        ObjectPoolServiceTracker tracker;
        for ( final String id : dataSourceIds )
        {
            tracker = new ObjectPoolServiceTracker ( poolTracker, id, new ObjectPoolListener () {

                public void serviceRemoved ( final Object service, final Dictionary<?, ?> properties )
                {
                    handleRemoved ( id, properties, (DataSource)service );
                }

                public void serviceModified ( final Object service, final Dictionary<?, ?> properties )
                {
                    handleModified ( id, properties, (DataSource)service );
                }

                public void serviceAdded ( final Object service, final Dictionary<?, ?> properties )
                {
                    handleAdded ( id, properties, (DataSource)service );
                }
            } );
            this.trackers.add ( tracker );
        }
    }

    protected void handleAdded ( final String id, final Dictionary<?, ?> properties, final DataSource service )
    {
        this.listener.dataSourceAdded ( id, properties, service );
    }

    protected void handleModified ( final String id, final Dictionary<?, ?> properties, final DataSource service )
    {
        this.listener.dataSourceModified ( id, properties, service );
    }

    protected void handleRemoved ( final String id, final Dictionary<?, ?> properties, final DataSource service )
    {
        this.listener.dataSourceRemoved ( id, properties, service );
    }

    public synchronized void open ()
    {
        for ( final ObjectPoolServiceTracker tracker : this.trackers )
        {
            tracker.open ();
        }
    }

    public synchronized void close ()
    {
        for ( final ObjectPoolServiceTracker tracker : this.trackers )
        {
            tracker.close ();
        }
    }
}
