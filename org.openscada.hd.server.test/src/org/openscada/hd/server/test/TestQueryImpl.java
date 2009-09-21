package org.openscada.hd.server.test;

import java.util.Calendar;
import java.util.HashMap;
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

        startLoadData ( parameters );
    }

    private void startLoadData ( final QueryParameters parameters )
    {
        this.listener.updateState ( QueryState.LOADING );

        this.executor.execute ( new Runnable () {

            public void run ()
            {
                loadData ( parameters );
            }
        } );
    }

    protected void loadData ( final QueryParameters parameters )
    {
        this.listener.updateState ( QueryState.LOADING );

        final long startTix = parameters.getStartTimestamp ().getTimeInMillis ();
        final long countMillis = parameters.getEndTimestamp ().getTimeInMillis () - startTix;

        final double step = (double)countMillis / (double)parameters.getEntries ();

        for ( int i = 0; i < parameters.getEntries (); i++ )
        {

            final long startStep = (long) ( step * i );
            final long endStep = (long) ( step * ( i + 1 ) );
            final Calendar start = Calendar.getInstance ();
            start.setTimeInMillis ( startStep );

            final Calendar end = Calendar.getInstance ();
            end.setTimeInMillis ( endStep );

            final ValueInformation vi = new ValueInformation ( start, end, 1.0, 1 );
            final Map<String, Value[]> values = generateValues ( vi );

            this.listener.updateData ( i, values, new ValueInformation[] { vi } );

            try
            {
                Thread.sleep ( 100 );
            }
            catch ( final InterruptedException e )
            {
                Thread.currentThread ().interrupt ();
                return;
            }
        }

        this.listener.updateState ( QueryState.COMPLETE );
    }

    private Map<String, Value[]> generateValues ( final ValueInformation vi )
    {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double avg = 0.0;

        final long count = vi.getEndTimestamp ().getTimeInMillis () - vi.getStartTimestamp ().getTimeInMillis ();

        for ( long i = 0; i < count; i++ )
        {
            final double rad = Math.toRadians ( i ) / 1000.0;
            final double value = Math.sin ( rad );

            min = Math.min ( value, min );
            max = Math.max ( value, max );
            avg += value;
        }

        avg = avg / Double.valueOf ( count );

        final Map<String, Value[]> result = new HashMap<String, Value[]> ();
        result.put ( "AVG", new Value[] { new Value ( avg ) } );
        result.put ( "MIN", new Value[] { new Value ( min ) } );
        result.put ( "MAX", new Value[] { new Value ( max ) } );
        return result;
    }

    public void close ()
    {
        logger.info ( "Close query" );

        this.executor.shutdownNow ();

        this.listener.updateState ( QueryState.DISCONNECTED );
    }

    public void updateParameters ( final QueryParameters parameters )
    {
        startLoadData ( parameters );
    }
}
