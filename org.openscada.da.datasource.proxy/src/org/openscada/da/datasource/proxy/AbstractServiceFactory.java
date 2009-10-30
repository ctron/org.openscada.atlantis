package org.openscada.da.datasource.proxy;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ca.ConfigurationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public abstract class AbstractServiceFactory implements ConfigurationFactory
{

    private final Map<String, Service> instances = new HashMap<String, Service> ();

    private final Map<String, ServiceRegistration> regs = new HashMap<String, ServiceRegistration> ();

    private final BundleContext context;

    public AbstractServiceFactory ( final BundleContext context )
    {
        this.context = context;
    }

    public synchronized void dispose ()
    {
        for ( final ServiceRegistration reg : this.regs.values () )
        {
            reg.unregister ();
        }
        for ( final Service service : this.instances.values () )
        {
            service.dispose ();
        }
        this.instances.clear ();
        this.regs.clear ();
    }

    public synchronized void delete ( final String configurationId ) throws Exception
    {
        final ServiceRegistration reg = this.regs.remove ( configurationId );
        if ( reg != null )
        {
            reg.unregister ();
        }

        final Service service = this.instances.remove ( configurationId );

        if ( service != null )
        {
            service.dispose ();
        }
    }

    public synchronized void update ( final String configurationId, final Map<String, String> properties ) throws Exception
    {
        final Service service = this.instances.get ( configurationId );
        if ( service != null )
        {
            // update
            service.update ( properties );
        }
        else
        {
            // create
            final Service newService = createService ( configurationId, properties );
            if ( newService != null )
            {
                final ServiceRegistration reg = registerService ( this.context, configurationId, newService );

                if ( reg != null )
                {
                    this.regs.put ( configurationId, reg );
                    this.instances.put ( configurationId, newService );
                }
                else
                {
                    newService.dispose ();
                }
            }
        }
    }

    protected abstract Service createService ( String configurationId, Map<String, String> properties ) throws Exception;

    protected abstract ServiceRegistration registerService ( BundleContext context, String configurationId, Service service );

}
