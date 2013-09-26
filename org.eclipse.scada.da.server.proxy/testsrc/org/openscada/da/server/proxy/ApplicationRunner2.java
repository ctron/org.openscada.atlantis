package org.eclipse.scada.da.server.proxy;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.eclipse.scada.da.server.proxy.item.ProxyValueHolder;
import org.eclipse.scada.da.server.proxy.utils.ProxyPrefixName;
import org.eclipse.scada.da.server.proxy.utils.ProxySubConnectionId;

public class ApplicationRunner2
{
    @SuppressWarnings ( "unused" )
    private static Logger logger = Logger.getLogger ( ApplicationRunner2.class );

    private static final int ITERATIONS = 1000000;

    private static final int VARIANCE = 1;

    private static final List<ProxySubConnectionId> connections = Arrays.asList ( // 
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

        RecordItemUpdateListener listener;
        pvh.setListener ( listener = new RecordItemUpdateListener () );

        final ExecutorService e = Executors.newFixedThreadPool ( 30 );

        final Collection<Callable<Object>> calls = new LinkedList<Callable<Object>> ();

        listener.switchTo ( 1, 2, 3 );

        calls.add ( new SequenceWriter ( connections.get ( 0 ), pvh, 1, 2, 3 ) );
        // calls.add ( new RecordedSwitcher () );
        calls.add ( new SequenceWriter ( connections.get ( 1 ), pvh, 4, 5, 6 ) );
        calls.add ( new SequenceWriter ( connections.get ( 2 ), pvh, 7, 8, 9 ) );

        final long start = System.currentTimeMillis ();
        e.invokeAll ( calls );
        final long end = System.currentTimeMillis ();
        e.shutdown ();

        listener.dump ();

        System.out.println ( String.format ( "%s operations in %s ms (%.2f)", ApplicationRunner1.operations, end - start, (double)ApplicationRunner1.operations / (double) ( end - start ) ) );
    }
}
