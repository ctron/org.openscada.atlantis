package org.openscada.hd.server.storage.master.hds;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.openscada.hd.server.storage.master.hds.console.CommandProviderImpl;
import org.openscada.hds.DataFilePool;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator
{

    private final static Logger logger = LoggerFactory.getLogger ( Activator.class );

    private static BundleContext context;

    private StorageManager manager;

    private DataFilePool pool;

    static BundleContext getContext ()
    {
        return context;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext bundleContext ) throws Exception
    {
        Activator.context = bundleContext;

        this.pool = new DataFilePool ( Integer.getInteger ( "org.openscada.hd.server.storage.hds.instanceCountTarget", 10 ) );

        this.manager = new StorageManager ( bundleContext, this.pool );
        registerConsole ();
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {
        this.manager.dispose ();
        this.manager = null;

        this.pool.dispose ();

        Activator.context = null;
    }

    private void registerConsole ()
    {
        try
        {
            final CommandProvider provider = new CommandProviderImpl ( this.manager );
            final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
            context.registerService ( CommandProvider.class.getName (), provider, properties );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to register Equinox console", e );
        }
    }

}
