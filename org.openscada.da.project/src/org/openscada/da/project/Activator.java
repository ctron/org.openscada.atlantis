package org.openscada.da.project;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin
{

    public static String PLUGIN_ID = "org.openscada.da.project";

    private static Activator instance = null;

    private ConnectionManager connectionManager;

    public Activator ()
    {

    }

    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        super.start ( context );
        instance = this;

        this.connectionManager = new ConnectionManager ();
    }

    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        this.connectionManager.dispose ();
        this.connectionManager = null;

        instance = null;
        super.stop ( context );
    }

    public static Activator getDefault ()
    {
        return instance;
    }

    public static ConnectionManager getConnectionManager ()
    {
        return instance.connectionManager;
    }

}
