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

import org.eclipse.core.runtime.Status;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.ui.connection.AbstractConnectionDiscoverer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ResourceDiscoverer extends AbstractConnectionDiscoverer
{

    private final static Logger logger = LoggerFactory.getLogger ( ResourceDiscoverer.class );

    public ResourceDiscoverer ()
    {
        initialize ();
    }

    protected abstract void initialize ();

    protected void load ( final File file )
    {
        logger.info ( "Loading: {}", file );

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

}
