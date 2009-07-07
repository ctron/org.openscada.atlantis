package org.openscada.da.client.connector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class ConnectorInitializer
{
    private static boolean initialized = false;

    private static Object sync = new Object ();

    public static void initialize () throws CoreException
    {
        // pre-check .. not synchronized
        if ( initialized )
        {
            return;
        }

        synchronized ( sync )
        {
            // check ... synchronized
            if ( initialized )
            {
                return;
            }

            initialized = true;

            final IExtensionRegistry registry = Platform.getExtensionRegistry ();
            final IExtensionPoint point = registry.getExtensionPoint ( "org.openscada.da.client.connector" );

            for ( final IExtension extension : point.getExtensions () )
            {
                for ( final IConfigurationElement config : extension.getConfigurationElements () )
                {
                    if ( "driver".equals ( config.getName () ) )
                    {
                        final ConnectorLoader loader = (ConnectorLoader)config.createExecutableExtension ( "class" );
                        if ( loader != null )
                        {
                            loader.load ();
                        }
                    }
                }
            }

        }
    }
}
