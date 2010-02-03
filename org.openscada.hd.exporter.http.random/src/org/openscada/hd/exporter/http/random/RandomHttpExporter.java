package org.openscada.hd.exporter.http.random;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.openscada.hd.exporter.http.DataPoint;
import org.openscada.hd.exporter.http.HttpExporter;

public class RandomHttpExporter implements HttpExporter
{
    private final int range = 800;

    private final double probability = ( 1.0 / 9.0 ) * 8.5;

    public List<DataPoint> getData ( final String item, final String type, final Date from, final Date to, final Integer number )
    {
        List<DataPoint> result = new ArrayList<DataPoint> ();
        int seed = item.hashCode () + type.hashCode () + from.hashCode () + to.hashCode () + number.hashCode ();
        final Random rndData = new Random ( seed );
        final Random rndQuality = new Random ( seed * 13 );
        final Random rndManual = new Random ( seed * 17 );
        double[] d = genTimeSeriesData ( rndData, number );
        double[] q = genTimeSeriesMeta ( rndQuality, number );
        double[] m = genTimeSeriesMeta ( rndManual, number );
        double delta = ( ( to.getTime () - from.getTime () ) / ( (double)number ) );
        for ( int i = 0; i < d.length; i++ )
        {
            DataPoint dp = new DataPoint ();
            dp.setValue ( d[i] );
            dp.setQuality ( q[i] );
            dp.setManual ( m[i] );
            dp.setTimestamp ( new Date ( Math.round ( from.getTime () + i * delta ) ) );
            result.add ( dp );
        }
        return result;
    }

    private double[] genTimeSeriesData ( final Random rnd, final int number )
    {
        double[] result = new double[number];
        double v = rnd.nextDouble () * this.range;
        for ( int i = 0; i < number; i++ )
        {
            result[i] = v + ( rnd.nextBoolean () ? -rnd.nextDouble () * 5 : rnd.nextDouble () * 5 );
        }
        return result;
    }

    private double[] genTimeSeriesMeta ( final Random rnd, final int number )
    {
        double[] result = new double[number];
        double v = 1.0;
        int pr = (int)Math.round ( this.probability * number );
        for ( int i = 0; i < number; i++ )
        {
            boolean p = rnd.nextInt ( number ) > pr;
            if ( v < 1.0 )
            {
                if ( p )
                {
                    v = 1.0;
                }
            }
            else
            {
                if ( p )
                {
                    v = rnd.nextDouble ();
                }
            }
            result[i] = v;
        }
        return result;
    }

    public List<String> getItems ()
    {
        return new ArrayList<String> ();
    }

    public List<String> getSeries ( final String itemId )
    {
        return new ArrayList<String> ();
    }
}
