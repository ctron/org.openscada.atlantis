package org.openscada.da.server.proxy;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.server.proxy.item.ProxyValueHolder;
import org.openscada.da.server.proxy.utils.ProxyPrefixName;
import org.openscada.da.server.proxy.utils.ProxySubConnectionId;

public class ApplicationRunner1
{
    private static Logger logger = Logger.getLogger ( ApplicationRunner1.class );

    private static final int ITERATIONS = 1000000;

    private static final int VARIANCE = 1;

    static long operations = 0;

    private static final Collection<ProxySubConnectionId> connections = Arrays.asList ( // 
    new ProxySubConnectionId ( "sub1" ), //
    new ProxySubConnectionId ( "sub2" ), //
    new ProxySubConnectionId ( "sub3" ) //
    );

    public static int getCount ()
    {
        final Random r = new Random ();
        return ITERATIONS + r.nextInt ( VARIANCE );
    }

    public static void main ( final String[] args ) throws InterruptedException
    {
        final ProxySubConnectionId connectionId = new ProxySubConnectionId ( "sub1" );
        final ProxyValueHolder pvh = new ProxyValueHolder ( ".", new ProxyPrefixName ( "prefix" ), connectionId, "item1" );

        pvh.setListener ( new ItemUpdateListener () {

            public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
            {
            }

            public void notifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
            {
            }
        } );

        final ExecutorService e = Executors.newFixedThreadPool ( 30 );

        final Collection<Callable<Object>> calls = new LinkedList<Callable<Object>> ();

        for ( final ProxySubConnectionId connection : connections )
        {
            logger.info ( "String connection: " + connection.getName () );

            calls.add ( new Writer ( connection, pvh ) );
            calls.add ( new Writer ( connection, pvh ) );
            calls.add ( new Writer ( connection, pvh ) );
            calls.add ( new Writer ( connection, pvh ) );

            calls.add ( new SubscriptionChanger ( connection, pvh ) );
            calls.add ( new SubscriptionChanger ( connection, pvh ) );

            calls.add ( new Switcher ( connection, pvh ) );
        }

        final long start = System.currentTimeMillis ();

        e.invokeAll ( calls );

        final long end = System.currentTimeMillis ();

        e.shutdown ();

        for ( final ProxySubConnectionId connection : connections )
        {
            pvh.switchTo ( connection );
            System.out.println ( "=======================================" );
            System.out.println ( "Connection: " + connection.getName () );
            System.out.println ( "Value: " + pvh.getValue () );
            System.out.println ( "Current state: " + pvh.getCurrentValue ().getSubscriptionState () );
        }

        System.out.println ( String.format ( "%s operations in %s ms (%.2f)", operations, end - start, (double)operations / (double) ( end - start ) ) );
    }
}
