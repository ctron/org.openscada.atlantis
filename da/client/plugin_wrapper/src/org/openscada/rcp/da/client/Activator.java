package org.openscada.rcp.da.client;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin
{

    public Activator ()
    {
    }
    
    @Override
    public void start ( BundleContext context ) throws Exception
    {
        super.start ( context );
        
        System.err.println ( "Loading..." );
        
        IExtensionRegistry registry = Platform.getExtensionRegistry ();
        IExtensionPoint point = registry.getExtensionPoint ( "org.openscada.da.client.connector" );
        
        for ( IExtension extension : point.getExtensions () )
        {
            for ( IConfigurationElement config : extension.getConfigurationElements () )
            {
                ConnectorLoader loader = (ConnectorLoader)config.createExecutableExtension ( "class" );
                if ( loader != null )
                {
                    System.err.println ( "Loading" );
                    loader.load ();
                }
            }
        }
    }

}
