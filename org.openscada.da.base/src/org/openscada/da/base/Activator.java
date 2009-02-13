package org.openscada.da.base;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.openscada.da.base.connection.ConnectionManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

    // The plug-in ID
    public static final String PLUGIN_ID = "org.openscada.da.base"; //$NON-NLS-1$

    // The shared instance
    private static Activator plugin;

    private ConnectionManager connectionManager;

    /**
     * The constructor
     */
    public Activator ()
    {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        super.start ( context );
        plugin = this;

        this.connectionManager = new ConnectionManager ();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        this.connectionManager.dispose ();
        this.connectionManager = null;

        plugin = null;
        super.stop ( context );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault ()
    {
        return plugin;
    }

    public static ConnectionManager getConnectionManager ()
    {
        return plugin.connectionManager;
    }

}
