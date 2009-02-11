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

public class TestApplication1
{
    private static Logger logger = Logger.getLogger ( TestApplication1.class );

    private static final int ITERATIONS = 1000000;

    private static final int VARIANCE = 100000;

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

    private static final class SubscriptionChanger implements Callable<Object>
    {

        private final ProxySubConnectionId connectionId;

        private final ProxyValueHolder pvh;

        private SubscriptionChanger ( final ProxySubConnectionId connectionId, final ProxyValueHolder pvh )
        {
            this.connectionId = connectionId;
            this.pvh = pvh;
        }

        public Object call () throws Exception
        {
            final SubscriptionState[] states = SubscriptionState.values ();

            for ( int i = 0; i < getCount (); i++ )
            {
                this.pvh.updateSubscriptionState ( this.connectionId, states[i % states.length], null );
            }
            this.pvh.updateSubscriptionState ( this.connectionId, SubscriptionState.CONNECTED, null );

            return null;
        }
    }

    private static final class Switcher implements Callable<Object>
    {

        private final ProxySubConnectionId connectionId;

        private final ProxyValueHolder pvh;

        private Switcher ( final ProxySubConnectionId connectionId, final ProxyValueHolder pvh )
        {
            this.connectionId = connectionId;
            this.pvh = pvh;
        }

        public Object call () throws Exception
        {
            for ( int i = 0; i < getCount (); i++ )
            {
                this.pvh.switchTo ( this.connectionId );
            }

            return null;
        }
    }

    private static final class Writer implements Callable<Object>
    {
        private final ProxySubConnectionId connectionId;

        private final ProxyValueHolder pvh;

        private Writer ( final ProxySubConnectionId connectionId, final ProxyValueHolder pvh )
        {
            this.connectionId = connectionId;
            this.pvh = pvh;
        }

        public Object call () throws Exception
        {
            try
            {
                for ( int i = 0; i < getCount (); i++ )
                {
                    this.pvh.updateData ( this.connectionId, new Variant ( i ), null, null );
                }
                this.pvh.updateData ( this.connectionId, new Variant ( "complete" ), null, null );
            }
            catch ( final Throwable e )
            {
                e.printStackTrace ();
            }

            return null;
        }
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

        final ExecutorService e = Executors.newFixedThreadPool ( 10 );

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

        e.invokeAll ( calls );

        e.shutdown ();

        for ( final ProxySubConnectionId connection : connections )
        {
            pvh.switchTo ( connection );
            System.out.println ( "=======================================" );
            System.out.println ( "Connection: " + connection.getName () );
            System.out.println ( "Value: " + pvh.getValue () );
            System.out.println ( "Current state: " + pvh.getCurrentValue ().getSubscriptionState () );
        }
    }
}
