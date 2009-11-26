package org.openscada.da.server.dave.factory;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ca.ConfigurationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public abstract class AbstractServiceConfigurationFactory implements ConfigurationFactory
{

    private final Map<String, Entry> services = new HashMap<String, Entry> ();

    private final BundleContext context;

    protected static class Entry
    {
        private final Object service;

        private final ServiceRegistration handle;

        public Entry ( final Object service, final ServiceRegistration handle )
        {
            this.service = service;
            this.handle = handle;
        }

        public ServiceRegistration getHandle ()
        {
            return this.handle;
        }

        public Object getService ()
        {
            return this.service;
        }
    }

    public AbstractServiceConfigurationFactory ( final BundleContext context )
    {
        this.context = context;
    }

    public void dispose ()
    {
        for ( final Entry entry : this.services.values () )
        {
            disposeService ( entry.getService () );
            entry.getHandle ().unregister ();
        }
    }

    public void delete ( final String configurationId ) throws Exception
    {
        final Entry entry = this.services.remove ( configurationId );
        if ( entry != null )
        {
            disposeService ( entry.getService () );
            entry.getHandle ().unregister ();
        }
    }

    public void update ( final String configurationId, final Map<String, String> parameters ) throws Exception
    {
        Entry entry = this.services.get ( configurationId );
        if ( entry != null )
        {
            updateService ( entry, parameters );
        }
        else
        {
            entry = createService ( configurationId, this.context, parameters );
            if ( entry != null )
            {
                this.services.put ( configurationId, entry );
            }
        }
    }

    protected abstract Entry createService ( String configurationId, BundleContext context, final Map<String, String> parameters ) throws Exception;

    protected abstract void disposeService ( Object service );

    protected abstract void updateService ( Entry entry, Map<String, String> parameters ) throws Exception;
}
