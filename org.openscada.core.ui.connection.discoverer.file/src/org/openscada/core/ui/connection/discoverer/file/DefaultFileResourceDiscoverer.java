package org.openscada.core.ui.connection.discoverer.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.ui.connection.ConnectionStore;

public class DefaultFileResourceDiscoverer extends ResourceDiscoverer implements ConnectionStore
{
    private static final String FILENAME = "connections.txt";

    @Override
    protected void initialize ()
    {
        load ( getFile () );
    }

    public void add ( final ConnectionInformation connectionInformation ) throws CoreException
    {
        if ( addConnection ( connectionInformation ) )
        {
            store ();
        }
    }

    public void remove ( final ConnectionInformation connectionInformation ) throws CoreException
    {
        if ( removeConnection ( connectionInformation ) )
        {
            store ();
        }
    }

    private void store () throws CoreException
    {
        PrintWriter printer = null;
        try
        {
            printer = new PrintWriter ( getFile () );
            for ( final ConnectionInformation info : this.getConnections () )
            {
                printer.println ( info.toString () );
            }
        }
        catch ( final IOException e )
        {
            throw new CoreException ( new Status ( Status.ERROR, Activator.PLUGIN_ID, "Failed to store connections", e ) );
        }
        finally
        {
            if ( printer != null )
            {
                printer.close ();
            }
        }
    }

    private File getFile ()
    {
        return Activator.getDefault ().getBundle ().getBundleContext ().getDataFile ( FILENAME );
    }

}
