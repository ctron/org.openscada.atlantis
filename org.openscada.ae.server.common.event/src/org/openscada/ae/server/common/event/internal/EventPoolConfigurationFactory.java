package org.openscada.ae.server.common.event.internal;

import java.util.Map;

import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.osgi.framework.BundleContext;

public class EventPoolConfigurationFactory extends AbstractServiceConfigurationFactory<EventPoolManager>
{

    public EventPoolConfigurationFactory ( final BundleContext context )
    {
        super ( context );
    }

    @Override
    protected Entry<EventPoolManager> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final String filter = parameters.get ( "filter" );
        final Integer size = Integer.parseInt ( parameters.get ( "size" ) );

        final EventPoolManager manager = new EventPoolManager ( context, configurationId, filter, size );
        return new Entry<EventPoolManager> ( manager );
    }

    @Override
    protected void disposeService ( final EventPoolManager service )
    {
        service.dispose ();
    }

    @Override
    protected Entry<EventPoolManager> updateService ( final String configurationId, final Entry<EventPoolManager> entry, final Map<String, String> parameters ) throws Exception
    {
        final String filter = parameters.get ( "filter" );
        final Integer size = Integer.parseInt ( parameters.get ( "size" ) );

        entry.getService ().update ( filter, size );
        return entry;
    }

}
