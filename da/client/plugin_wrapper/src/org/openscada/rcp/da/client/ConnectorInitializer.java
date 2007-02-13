package org.openscada.rcp.da.client;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class ConnectorInitializer
{
    private static boolean _initialized = false;
    private static Object _sync = new Object ();
    
    public static void initialize ( ) throws CoreException
    {
        // pre-check .. not synchronized
        if ( _initialized )
            return;
        
        synchronized ( _sync )
        {
            // check ... synchronized
            if ( _initialized )
                return;
            
            _initialized = true;
            
            IExtensionRegistry registry = Platform.getExtensionRegistry ();
            IExtensionPoint point = registry.getExtensionPoint ( "org.openscada.da.client.connector" );

            for ( IExtension extension : point.getExtensions () )
            {
                for ( IConfigurationElement config : extension.getConfigurationElements () )
                {
                    ConnectorLoader loader = (ConnectorLoader)config.createExecutableExtension ( "class" );
                    if ( loader != null )
                    {
                        loader.load ();
                    }
                }
            }

        }
    }
}
