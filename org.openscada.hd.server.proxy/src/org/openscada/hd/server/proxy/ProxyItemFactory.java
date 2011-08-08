package org.openscada.hd.server.proxy;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.hd.server.common.HistoricalItem;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class ProxyItemFactory extends AbstractServiceConfigurationFactory<ProxyHistoricalItem>
{

    private final Executor executor;

    public ProxyItemFactory ( final BundleContext context, final Executor executor )
    {
        super ( context );
        this.executor = executor;
    }

    @Override
    protected Entry<ProxyHistoricalItem> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final ProxyHistoricalItem service = new ProxyHistoricalItem ( context, this.executor, configurationId, parameters );

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
        properties.put ( Constants.SERVICE_DESCRIPTION, "A proxy to historical items" );
        properties.put ( Constants.SERVICE_PID, configurationId );
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );

        final ServiceRegistration<HistoricalItem> handle = context.registerService ( HistoricalItem.class, service, properties );
        return new Entry<ProxyHistoricalItem> ( configurationId, service, handle );
    }

    @Override
    protected void disposeService ( final String configurationId, final ProxyHistoricalItem service )
    {
        service.dispose ();
    }

    @Override
    protected Entry<ProxyHistoricalItem> updateService ( final String configurationId, final Entry<ProxyHistoricalItem> entry, final Map<String, String> parameters ) throws Exception
    {
        final BundleContext context = entry.getHandle ().getReference ().getBundle ().getBundleContext ();
        entry.getHandle ().unregister ();
        disposeService ( configurationId, entry.getService () );
        return createService ( configurationId, context, parameters );
    }
}
