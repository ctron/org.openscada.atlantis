package org.openscada.da.client.connector;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.Connection;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin
{

    public static final String PLUGIN_ID = "org.openscada.da.client.connector";

    public static final String NATIVE_LS = System.getProperty ( "line.separator", "\n" );

    private static Activator instance;

    public Activator ()
    {
    }

    public static Activator getDefault ()
    {
        return instance;
    }

    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        super.start ( context );
        instance = this;

        try
        {
            ConnectorInitializer.initialize ();
        }
        catch ( final Throwable e )
        {
            this.getLog ().log ( new Status ( IStatus.ERROR, PLUGIN_ID, "Failed to initialize connectors", e ) );
        }
    }

    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        instance = null;
        super.stop ( context );
    }

    public static Connection createConnection ( final ConnectionInformation ci )
    {
        return ConnectorHelper.createConnection ( ci );
    }
}
