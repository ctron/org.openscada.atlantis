package org.openscada.ae.server.common.condition.internal;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.monitor.ConditionListener;
import org.openscada.ae.monitor.MonitorService;
import org.openscada.ae.server.common.condition.ConditionQuery;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.osgi.pool.AllObjectPoolServiceTracker;
import org.openscada.utils.osgi.pool.ObjectPoolListener;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleConditionQuery extends ConditionQuery implements ConditionListener
{

    private final static Logger logger = LoggerFactory.getLogger ( BundleConditionQuery.class );

    private final Set<MonitorService> services = new HashSet<MonitorService> ();

    private final AllObjectPoolServiceTracker tracker;

    private final Map<String, ConditionStatusInformation> cachedData = new HashMap<String, ConditionStatusInformation> ();

    private Filter filter;

    public BundleConditionQuery ( final BundleContext context, final ObjectPoolTracker poolTracker ) throws InvalidSyntaxException
    {
        this.tracker = new AllObjectPoolServiceTracker ( poolTracker, new ObjectPoolListener () {

            public void serviceRemoved ( final Object service, final Dictionary<?, ?> properties )
            {
                BundleConditionQuery.this.handleRemoved ( (MonitorService)service );
            }

            public void serviceModified ( final Object service, final Dictionary<?, ?> properties )
            {
            }

            public void serviceAdded ( final Object service, final Dictionary<?, ?> properties )
            {
                BundleConditionQuery.this.handleAdded ( (MonitorService)service );
            }
        } );
        this.tracker.open ();
    }

    protected synchronized void handleAdded ( final MonitorService service )
    {
        if ( this.services.add ( service ) )
        {
            service.addStatusListener ( this );
        }
    }

    protected synchronized void handleRemoved ( final MonitorService service )
    {
        if ( this.services.remove ( service ) )
        {
            service.removeStatusListener ( this );

            this.cachedData.remove ( service.getId () );
            updateData ( null, new String[] { service.getId () } );
        }
    }

    public void update ( final Map<String, String> parameters )
    {
        setFilter ( Filter.EMPTY );
    }

    /**
     * Sets the new filter and actualizes the data set
     */
    protected synchronized void setFilter ( final Filter filter )
    {
        this.filter = filter;
        setData ( getFiltered () );
    }

    protected synchronized ConditionStatusInformation[] getFiltered ()
    {
        final List<ConditionStatusInformation> result = new ArrayList<ConditionStatusInformation> ();

        for ( final ConditionStatusInformation ci : this.cachedData.values () )
        {
            if ( matchesFilter ( ci ) )
            {
                result.add ( ci );
            }
        }

        return result.toArray ( new ConditionStatusInformation[result.size ()] );
    }

    private boolean matchesFilter ( final ConditionStatusInformation status )
    {

        // TODO Auto-generated method stub
        return true;
    }

    public synchronized void dispose ()
    {
        super.dispose ();
        for ( final MonitorService service : this.services )
        {
            service.removeStatusListener ( this );
        }

        this.services.clear ();
        this.tracker.close ();

    }

    public synchronized void statusChanged ( final ConditionStatusInformation status )
    {
        if ( logger.isDebugEnabled () )
        {
            logger.debug ( "Status changed: " + status );
        }

        this.cachedData.put ( status.getId (), status );

        if ( matchesFilter ( status ) )
        {
            updateData ( new ConditionStatusInformation[] { status }, null );
        }
    }
}
