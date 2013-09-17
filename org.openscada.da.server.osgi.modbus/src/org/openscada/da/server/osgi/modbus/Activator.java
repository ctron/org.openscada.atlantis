package org.openscada.da.server.osgi.modbus;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import org.eclipse.scada.utils.concurrent.ExportedExecutorService;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

    private MasterFactory masterFactory;

    private SlaveFactory slaveFactory;

    private ServiceRegistration<ConfigurationFactory> masterFactoryHandle;

    private ServiceRegistration<ConfigurationFactory> slaveFactoryHandle;

    private ExportedExecutorService executor;

    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        this.executor = new ExportedExecutorService ( "org.openscada.da.server.osgi.modbus", 1, 1, 1, TimeUnit.MINUTES );

        this.masterFactory = new MasterFactory ( context );
        this.slaveFactory = new SlaveFactory ( context, this.masterFactory, this.executor );

        {
            final Dictionary<String, Object> properties = new Hashtable<> ();
            properties.put ( Constants.SERVICE_DESCRIPTION, "Modbus master device factory" );
            properties.put ( Constants.SERVICE_VENDOR, "openSCADA.org" );
            properties.put ( ConfigurationAdministrator.FACTORY_ID, "org.openscada.da.server.osgi.modbus.masterDevice" );
            this.masterFactoryHandle = context.registerService ( ConfigurationFactory.class, this.masterFactory, properties );
        }

        {
            final Dictionary<String, Object> properties = new Hashtable<> ();
            properties.put ( Constants.SERVICE_DESCRIPTION, "Modbus slave device factory" );
            properties.put ( Constants.SERVICE_VENDOR, "openSCADA.org" );
            properties.put ( ConfigurationAdministrator.FACTORY_ID, "org.openscada.da.server.osgi.modbus.slaveDevice" );
            this.slaveFactoryHandle = context.registerService ( ConfigurationFactory.class, this.slaveFactory, properties );
        }
    }

    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        this.masterFactoryHandle.unregister ();
        this.masterFactory.dispose ();

        this.slaveFactoryHandle.unregister ();
        this.slaveFactory.dispose ();

        this.executor.shutdown ();
    }

}
