package org.openscada.da.master.common.manual;

import java.util.Map;

import org.openscada.da.master.AbstractMasterHandlerImpl;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

public class ManualHandlerFactoryImpl extends AbstractServiceConfigurationFactory<AbstractMasterHandlerImpl>
{
    public static final String FACTORY_ID = "org.openscada.da.manual";

    private final int priority;

    private final ObjectPoolTracker poolTracker;

    private final ServiceTracker caTracker;

    public ManualHandlerFactoryImpl ( final BundleContext context, final ObjectPoolTracker poolTracker, final ServiceTracker caTracker, final int priority ) throws InvalidSyntaxException
    {
        super ( context );
        this.priority = priority;
        this.poolTracker = poolTracker;
        this.caTracker = caTracker;
    }

    @Override
    public synchronized void dispose ()
    {
        this.poolTracker.close ();
        super.dispose ();
    }

    @Override
    protected Entry<AbstractMasterHandlerImpl> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final AbstractMasterHandlerImpl handler = new ManualHandlerImpl ( configurationId, this.poolTracker, this.priority, this.caTracker );
        handler.update ( parameters );
        return new Entry<AbstractMasterHandlerImpl> ( configurationId, handler );
    }

    @Override
    protected Entry<AbstractMasterHandlerImpl> updateService ( final String configurationId, final Entry<AbstractMasterHandlerImpl> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

    @Override
    protected void disposeService ( final String id, final AbstractMasterHandlerImpl service )
    {
        service.dispose ();
    }

}
