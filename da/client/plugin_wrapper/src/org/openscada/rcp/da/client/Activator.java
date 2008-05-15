package org.openscada.rcp.da.client;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin
{

    private static Activator instance;

    public Activator ()
    {
    }

    public static Activator getDefault ()
    {
        return instance;
    }

    @Override
    public void start ( BundleContext context ) throws Exception
    {
        super.start ( context );
        instance = this;

        try
        {
            ConnectorInitializer.initialize ();
        }
        catch ( Throwable e )
        {
            e.printStackTrace ();
        }
    }

    @Override
    public void stop ( BundleContext context ) throws Exception
    {
        instance = null;
        super.stop ( context );
    }

}
