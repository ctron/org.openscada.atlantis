package org.openscada.hd.server.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openscada.hd.QueryListener;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;

public class ValueBuffer
{

    private final QueryListener listener;

    private final long start;

    private final long end;

    public Collection<Double> values;

    private final int index;

    public ValueBuffer ( final QueryListener listener, final int index, final long start, final long end )
    {
        this.listener = listener;
        this.start = start;
        this.end = end;
        this.index = index;

        this.values = new ArrayList<Double> ();
    }

    public void pushData ( final double d )
    {
        this.values.add ( d );
    }

    public void sendData ()
    {
        BigDecimal dec = new BigDecimal ( 0.0 );

        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;

        if ( !this.values.isEmpty () )
        {
            for ( final Double d : this.values )
            {
                dec = dec.add ( new BigDecimal ( d ) );
                min = Math.min ( min, d );
                max = Math.max ( max, d );
            }
            dec = dec.divide ( new BigDecimal ( this.values.size () ), BigDecimal.ROUND_HALF_UP );
            sendData ( dec, min, max, 1.0 );
        }
        else
        {
            sendData ( null, Double.NaN, Double.NaN, 0.0 );
        }

    }

    private void sendData ( final BigDecimal avg, final double min, final double max, final double quality )
    {
        final Calendar startCal = Calendar.getInstance ();
        startCal.setTimeInMillis ( this.start );
        final Calendar endCal = Calendar.getInstance ();
        endCal.setTimeInMillis ( this.end );
        final ValueInformation info = new ValueInformation ( startCal, endCal, quality, this.values.size () );

        final Map<String, Value[]> values = new HashMap<String, Value[]> ();
        if ( avg == null )
        {
            values.put ( "AVG", new Value[] { new Value ( Double.NaN ) } );
        }
        else
        {
            values.put ( "AVG", new Value[] { new Value ( avg.doubleValue () ) } );
        }
        values.put ( "MIN", new Value[] { new Value ( min ) } );
        values.put ( "MAX", new Value[] { new Value ( max ) } );
        this.listener.updateData ( this.index, values, new ValueInformation[] { info } );
    }

}
