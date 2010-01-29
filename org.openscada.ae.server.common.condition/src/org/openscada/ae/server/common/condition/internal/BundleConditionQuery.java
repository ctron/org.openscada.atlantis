package org.openscada.ae.server.common.condition.internal;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.monitor.ConditionListener;
import org.openscada.ae.monitor.MonitorService;
import org.openscada.ae.server.common.condition.ConditionQuery;
import org.openscada.utils.osgi.pool.AllObjectPoolServiceTracker;
import org.openscada.utils.osgi.pool.ObjectPoolListener;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

public class BundleConditionQuery extends ConditionQuery implements ConditionListener
{
    private final static Logger logger = Logger.getLogger ( BundleConditionQuery.class );

    // private final Map<ServiceReference, MonitorService> services = new HashMap<ServiceReference, MonitorService> ();
    private final Set<MonitorService> services = new HashSet<MonitorService> ();

    private final AllObjectPoolServiceTracker tracker;

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
            updateData ( null, new String[] { service.getId () } );
        }
    }

    public void update ( final Map<String, String> parameters )
    {
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

    public void statusChanged ( final ConditionStatusInformation status )
    {
        if ( logger.isDebugEnabled () )
        {
            logger.debug ( "Status changed: " + status );
        }
        updateData ( new ConditionStatusInformation[] { status }, null );
    }
}
