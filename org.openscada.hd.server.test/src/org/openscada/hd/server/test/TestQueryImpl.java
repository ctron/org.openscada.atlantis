package org.openscada.hd.server.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestQueryImpl implements Query
{

    private final static Logger logger = LoggerFactory.getLogger ( TestQueryImpl.class );

    private final TestItemImpl item;

    private final QueryParameters parameters;

    private final QueryListener listener;

    private final ExecutorService executor;

    public TestQueryImpl ( final TestItemImpl item, final QueryParameters parameters, final QueryListener listener )
    {
        this.item = item;
        this.parameters = parameters;
        this.listener = listener;

        this.executor = Executors.newSingleThreadExecutor ();

        startLoadData ( this.parameters );
    }

    private void startLoadData ( final QueryParameters parameters )
    {
        this.listener.updateState ( QueryState.LOADING );

        if ( parameters.getEntries () == 0 )
        {
            this.listener.updateState ( QueryState.COMPLETE );
            return;
        }

        this.executor.execute ( new Runnable () {

            public void run ()
            {
                loadData ( parameters, 10 );
            }
        } );
    }

    protected void loadData ( final QueryParameters parameters, final int blockSize )
    {
        this.listener.updateParameters ( parameters, new HashSet<String> ( Arrays.asList ( "AVG", "MIN", "MAX" ) ) );

        final long startTix = parameters.getStartTimestamp ().getTimeInMillis ();
        final long endTix = parameters.getEndTimestamp ().getTimeInMillis ();
        final long countMillis = endTix - startTix;

        final double step = (double)countMillis / parameters.getEntries ();

        long currentTix = startTix;
        final List<ValueInformation> next = new ArrayList<ValueInformation> ();
        int count = 0;
        int startCount = 0;
        while ( true )
        {
            final Calendar start = Calendar.getInstance ();
            start.setTimeInMillis ( currentTix );

            final Calendar end = Calendar.getInstance ();
            long nextTix = currentTix + (long)step;
            if ( nextTix > endTix )
            {
                nextTix = endTix;
            }
            end.setTimeInMillis ( nextTix );

            next.add ( new ValueInformation ( start, end, 1.0, 1 ) );

            count++;
            if ( nextTix == endTix )
            {
                sendNext ( next, startCount );
                break;
            }
            else if ( count >= blockSize )
            {
                sendNext ( next, startCount );
                startCount += count;
                count = 0;
                next.clear ();
            }
            currentTix = nextTix;
        }

        /*
        for ( int i = 0; i < blocks; i++ )
        {
            final long startStep = (long) ( step * i * blockSize );
            final long endStep = (long) ( step * ( i * blockSize + 1 ) );

            final Calendar start = Calendar.getInstance ();
            start.setTimeInMillis ( startTix + startStep );

            final Calendar end = Calendar.getInstance ();
            end.setTimeInMillis ( startTix + endStep );

            logger.info ( "generating block: {}", new Object[] { String.format ( "%tc - %tc", start, end ) } );

            final Map<String, Value[]> values = new HashMap<String, Value[]> ();
            values.put ( "AVG", new Value[blockSize] );
            values.put ( "MIN", new Value[blockSize] );
            values.put ( "MAX", new Value[blockSize] );

            final ValueInformation[] infos = generateInfos ( start, end, blockSize );

            for ( int x = 0; x < infos.length; x++ )
            {
                generateValues ( x, values, infos[x] );
            }

            this.listener.updateData ( i * blockSize, values, infos );

        }
        */

        this.listener.updateState ( QueryState.COMPLETE );
    }

    private void sendNext ( final List<ValueInformation> next, final int sendIndex )
    {
        final int count = next.size ();
        if ( count > 0 )
        {
            logger.info ( "Sending {} entries: {} - {}", new Object[] { count, next.get ( 0 ), next.get ( count - 1 ) } );
        }
        final ValueInformation[] valueInformation = next.toArray ( new ValueInformation[count] );

        final Map<String, Value[]> values = new HashMap<String, Value[]> ();
        values.put ( "AVG", new Value[count] );
        values.put ( "MIN", new Value[count] );
        values.put ( "MAX", new Value[count] );

        int index = 0;
        for ( final ValueInformation info : valueInformation )
        {
            generateValues ( index, values, info );
            index++;
        }

        this.listener.updateData ( sendIndex, values, valueInformation );

    }

    private void generateValues ( final int index, final Map<String, Value[]> values, final ValueInformation vi )
    {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        BigDecimal avg = new BigDecimal ( 0.0 );

        final long start = vi.getStartTimestamp ().getTimeInMillis ();
        final long count = vi.getEndTimestamp ().getTimeInMillis () - start;

        for ( long i = 0; i < count; i++ )
        {

            final double d = i + start;
            final double value = Math.sin ( d / 100000.0 ) * 100.0;

            min = Math.min ( value, min );
            max = Math.max ( value, max );
            avg = avg.add ( new BigDecimal ( value ) );
        }

        avg = avg.divide ( new BigDecimal ( count ), BigDecimal.ROUND_HALF_UP );

        values.get ( "AVG" )[index] = new Value ( avg.doubleValue () );
        values.get ( "MIN" )[index] = new Value ( min );
        values.get ( "MAX" )[index] = new Value ( max );

    }

    public void close ()
    {
        logger.info ( "Close query" );

        this.executor.shutdownNow ();

        this.listener.updateState ( QueryState.DISCONNECTED );
    }

    public void changeParameters ( final QueryParameters parameters )
    {
        startLoadData ( parameters );
    }
}
