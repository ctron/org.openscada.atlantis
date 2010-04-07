package org.openscada.ae.server.common.condition.internal;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.openscada.ae.server.common.condition.ConditionQuery;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class QueryServiceFactory extends AbstractServiceConfigurationFactory<BundleMonitorQuery>
{
    public final static String FACTORY_ID = "ae.monitor.query";

    private final ObjectPoolTracker poolTracker;

    public QueryServiceFactory ( final BundleContext context, final ObjectPoolTracker poolTracker )
    {
        super ( context );
        this.poolTracker = poolTracker;
    }

    @Override
    protected Entry<BundleMonitorQuery> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final BundleMonitorQuery query = new BundleMonitorQuery ( context, this.poolTracker );
        query.update ( parameters );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( Constants.SERVICE_PID, configurationId );
        properties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );

        return new Entry<BundleMonitorQuery> ( configurationId, query, context.registerService ( ConditionQuery.class.getName (), query, properties ) );
    }

    @Override
    protected void disposeService ( final String configurationId, final BundleMonitorQuery service )
    {
        service.dispose ();
    }

    @Override
    protected Entry<BundleMonitorQuery> updateService ( final String configurationId, final Entry<BundleMonitorQuery> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return entry;
    }

}
