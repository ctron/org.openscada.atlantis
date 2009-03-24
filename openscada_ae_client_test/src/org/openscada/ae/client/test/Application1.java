package org.openscada.ae.client.test;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.openscada.ae.client.Connection;
import org.openscada.ae.client.ListQueryListener;
import org.openscada.ae.core.QueryDescriptor;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.AutoReconnectController;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;

public class Application1
{
    private static Logger logger = Logger.getLogger ( Application1.class );

    public static void main ( final String[] args ) throws InterruptedException
    {
        logger.info ( "Application1" );

        final Application1 app = new Application1 ();
        Thread.sleep ( 10000 );
        app.dispose ();
    }

    private void dispose ()
    {
        this.executor.shutdown ();
    }

    private final Connection connection;

    private final ExecutorService executor;

    public Application1 ()
    {
        this.executor = Executors.newSingleThreadExecutor ();

        this.connection = new org.openscada.ae.client.net.Connection ( ConnectionInformation.fromURI ( "ae:net://localhost:1301" ) );

        this.connection.addConnectionStateListener ( new ConnectionStateListener () {

            public void stateChange ( final org.openscada.core.client.Connection connection, final ConnectionState state, final Throwable error )
            {
                System.out.println ( "Connection state change: " + state );
                if ( error != null )
                {
                    error.printStackTrace ();
                }
                switch ( state )
                {
                case BOUND:
                    Application1.this.onBound ();
                }
            }
        } );

        final AutoReconnectController controller = new AutoReconnectController ( this.connection );
        controller.connect ();
    }

    protected void onBound ()
    {
        this.connection.listQueries ( new ListQueryListener () {

            public void handleError ( final Throwable error )
            {
                logger.warn ( "Failed to list queries", error );
            }

            public void handleSuccess ( final Collection<QueryDescriptor> result )
            {
                dumpQueries ( result );
                System.out.println ( "List queries complete" );
            }
        } );
    }

    protected void onBound1 ()
    {
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                listQueries ();
            }
        } );
    }

    protected void listQueries ()
    {
        try
        {
            System.out.println ( "Find queries..." );
            dumpQueries ( this.connection.listQueries () );
            System.out.println ( "Find queries...complete" );
        }
        catch ( final Throwable e )
        {
            System.out.println ( "Find queries failed" );
            e.printStackTrace ();
        }
    }

    private void dumpQueries ( final Collection<QueryDescriptor> listQueries )
    {
        for ( final QueryDescriptor desc : listQueries )
        {
            System.out.println ( "  Query: " + desc.getId () );
        }
    }
}
