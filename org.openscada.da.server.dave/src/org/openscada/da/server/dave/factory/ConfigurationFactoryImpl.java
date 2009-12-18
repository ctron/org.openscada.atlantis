package org.openscada.da.server.dave.factory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.openscada.da.server.dave.DaveDevice;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.osgi.framework.BundleContext;

public class ConfigurationFactoryImpl extends AbstractServiceConfigurationFactory<DaveDevice>
{

    private final Map<String, DaveDevice> devices = new HashMap<String, DaveDevice> ();

    private final BundleContext context;

    public ConfigurationFactoryImpl ( final BundleContext context )
    {
        super ( context );
        this.context = context;
    }

    public synchronized void delete ( final String configurationId ) throws Exception
    {
        final DaveDevice device = this.devices.remove ( configurationId );
        if ( device != null )
        {
            device.dispose ();
        }
    }

    public synchronized void update ( final String configurationId, final Map<String, String> properties ) throws Exception
    {
        DaveDevice device = this.devices.get ( configurationId );
        if ( device == null )
        {
            device = new DaveDevice ( this.context, configurationId, properties );
            this.devices.put ( configurationId, device );
        }
        else
        {
            device.update ( properties );
        }
    }

    @Override
    protected Entry<DaveDevice> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final DaveDevice device = new DaveDevice ( this.context, configurationId, parameters );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( "daveDevice", configurationId );
        return new Entry<DaveDevice> ( configurationId, device, context.registerService ( DaveDevice.class.getName (), device, properties ) );
    }

    @Override
    protected Entry<DaveDevice> updateService ( final String configurationId, final Entry<DaveDevice> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

    @Override
    protected void disposeService ( final String id, final DaveDevice service )
    {
        service.dispose ();
    }
}
