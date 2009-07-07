package org.openscada.da.client.connector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public class DriverAdapterInformation
{
    private final IConfigurationElement configurationElement;

    public DriverAdapterInformation ( final IConfigurationElement configurationElement )
    {
        this.configurationElement = configurationElement;
    }

    public String getName ()
    {
        return this.configurationElement.getAttribute ( "name" );
    }

    public DriverAdapter createDriverAdapter ()
    {
        try
        {
            return (DriverAdapter)this.configurationElement.createExecutableExtension ( "class" );
        }
        catch ( final CoreException e )
        {
            return null;
        }
    }

    public String getInterface ()
    {
        return this.configurationElement.getAttribute ( "interface" );
    }

    public String getType ()
    {
        return this.configurationElement.getAttribute ( "type" );
    }
}
