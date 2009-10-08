package org.openscada.core.ui.connection.discoverer.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.Status;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.ui.connection.ConnectionDiscoverer;
import org.openscada.core.ui.connection.ConnectionDiscoveryListener;

public abstract class ResourceDiscoverer implements ConnectionDiscoverer
{
    private final Set<ConnectionDiscoveryListener> listeners = new CopyOnWriteArraySet<ConnectionDiscoveryListener> ();

    protected Set<ConnectionInformation> connections = new HashSet<ConnectionInformation> ();

    public ResourceDiscoverer ()
    {
        initialize ();
    }

    protected abstract void initialize ();

    protected void load ( final File file )
    {
        try
        {
            load ( new FileInputStream ( file ) );
        }
        catch ( final FileNotFoundException e )
        {
            Activator.getDefault ().getLog ().log ( new Status ( Status.INFO, Activator.PLUGIN_ID, "Failed to load storage", e ) );
        }
    }

    protected void load ( final InputStream stream )
    {
        try
        {
            performLoad ( stream );
        }
        catch ( final Throwable e )
        {
            Activator.getDefault ().getLog ().log ( new Status ( Status.WARNING, Activator.PLUGIN_ID, "Failed to load storage", e ) );
        }
        finally
        {
            if ( stream != null )
            {
                try
                {
                    stream.close ();
                }
                catch ( final IOException e )
                {
                    Activator.getDefault ().getLog ().log ( new Status ( Status.ERROR, Activator.PLUGIN_ID, "Failed to close stream", e ) );
                }
            }
        }
    }

    private void performLoad ( final InputStream stream )
    {
        final Set<ConnectionInformation> result = new HashSet<ConnectionInformation> ();
        final LineNumberReader reader = new LineNumberReader ( new InputStreamReader ( stream ) );
        String line;
        try
        {
            while ( ( line = reader.readLine () ) != null )
            {
                final ConnectionInformation info = ConnectionInformation.fromURI ( line );
                if ( info != null )
                {
                    result.add ( info );
                }
            }
        }
        catch ( final IOException e )
        {
        }

        setConnections ( result );
    }

    protected synchronized void setConnections ( final Set<ConnectionInformation> result )
    {
        final Set<ConnectionInformation> added = new HashSet<ConnectionInformation> ( result );
        added.removeAll ( this.connections );

        final Set<ConnectionInformation> removed = new HashSet<ConnectionInformation> ( this.connections );
        removed.removeAll ( result );

        this.connections = result;

        fireDiscoveryUpdate ( added.toArray ( new ConnectionInformation[0] ), removed.toArray ( new ConnectionInformation[0] ) );
    }

    protected void fireDiscoveryUpdate ( final ConnectionInformation[] added, final ConnectionInformation[] removed )
    {
        for ( final ConnectionDiscoveryListener listener : this.listeners )
        {
            listener.discoveryUpdate ( added, removed );
        }
    }

    public void addConnectionListener ( final ConnectionDiscoveryListener listener )
    {
        if ( this.listeners.add ( listener ) )
        {
            listener.discoveryUpdate ( this.connections.toArray ( new ConnectionInformation[0] ), null );
        }
    }

    public void removeConnectionListener ( final ConnectionDiscoveryListener listener )
    {
        this.listeners.remove ( listener );
    }

}
