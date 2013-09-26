/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.hd.server.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.scada.hd.QueryListener;
import org.eclipse.scada.hd.data.ValueInformation;

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

        final ValueInformation info = new ValueInformation ( quality, 0.0, this.start, this.end, this.values.size () );

        final Map<String, List<Double>> values = new HashMap<String, List<Double>> ();
        if ( avg == null )
        {
            values.put ( "AVG", Arrays.asList ( Double.NaN ) );
        }
        else
        {
            values.put ( "AVG", Arrays.asList ( avg.doubleValue () ) );
        }
        values.put ( "MIN", Arrays.asList ( min ) );
        values.put ( "MAX", Arrays.asList ( max ) );

        this.listener.updateData ( this.index, values, Arrays.asList ( info ) );
    }

}
