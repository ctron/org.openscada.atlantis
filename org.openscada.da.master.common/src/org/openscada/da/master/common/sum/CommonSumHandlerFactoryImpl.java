package org.openscada.da.master.common.sum;

import java.util.Map;

import org.openscada.da.master.AbstractMasterHandlerImpl;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

public class CommonSumHandlerFactoryImpl extends AbstractServiceConfigurationFactory<AbstractMasterHandlerImpl>
{
    private final String tag;

    private final int priority;

    private final ObjectPoolTracker poolTracker;

    public CommonSumHandlerFactoryImpl ( final BundleContext context, final ObjectPoolTracker poolTracker, final String tag, final int priority ) throws InvalidSyntaxException
    {
        super ( context );
        this.tag = tag;
        this.priority = priority;
        this.poolTracker = poolTracker;
        this.poolTracker.open ();
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
        final AbstractMasterHandlerImpl handler = new CommonSumHandler ( this.poolTracker, this.tag, this.priority );
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
