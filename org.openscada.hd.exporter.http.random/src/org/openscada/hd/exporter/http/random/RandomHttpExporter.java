/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

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

    private final double probability = 1.0 / 9.0 * 8.5;

    public List<DataPoint> getData ( final String item, final String type, final Date from, final Date to, final Integer number )
    {
        final List<DataPoint> result = new ArrayList<DataPoint> ();
        final int seed = item.hashCode () + type.hashCode () + from.hashCode () + to.hashCode () + number.hashCode ();
        final Random rndData = new Random ( seed );
        final Random rndQuality = new Random ( seed * 13 );
        final Random rndManual = new Random ( seed * 17 );
        final double[] d = genTimeSeriesData ( rndData, number );
        final double[] q = genTimeSeriesMeta ( rndQuality, number );
        final double[] m = genTimeSeriesMeta ( rndManual, number );
        final double delta = ( to.getTime () - from.getTime () ) / (double)number;
        for ( int i = 0; i < d.length; i++ )
        {
            final DataPoint dp = new DataPoint ();
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
        final double[] result = new double[number];
        final double v = rnd.nextDouble () * this.range;
        for ( int i = 0; i < number; i++ )
        {
            result[i] = v + ( rnd.nextBoolean () ? -rnd.nextDouble () * 5 : rnd.nextDouble () * 5 );
        }
        return result;
    }

    private double[] genTimeSeriesMeta ( final Random rnd, final int number )
    {
        final double[] result = new double[number];
        double v = 1.0;
        final int pr = (int)Math.round ( this.probability * number );
        for ( int i = 0; i < number; i++ )
        {
            final boolean p = rnd.nextInt ( number ) > pr;
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
