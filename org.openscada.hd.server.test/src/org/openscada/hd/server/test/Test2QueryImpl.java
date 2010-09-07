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

package org.openscada.hd.server.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test2QueryImpl implements Query
{
    private final static Logger logger = LoggerFactory.getLogger ( Test2QueryImpl.class );

    private final Test2ItemImpl item;

    private QueryParameters parameters;

    private final QueryListener listener;

    private ValueBuffer[] values;

    private long start;

    private long end;

    private int count;

    private static Random random = new Random ();

    public Test2QueryImpl ( final Test2ItemImpl item, final QueryParameters parameters, final QueryListener listener )
    {
        this.item = item;
        this.parameters = parameters;
        this.listener = listener;

        this.start = parameters.getStartTimestamp ().getTimeInMillis ();
        this.end = parameters.getEndTimestamp ().getTimeInMillis ();
        this.count = parameters.getEntries ();

        initData ();
    }

    private void initData ()
    {
        this.listener.updateParameters ( this.parameters, new HashSet<String> ( Arrays.asList ( "AVG", "MIN", "MAX" ) ) );

        this.listener.updateState ( QueryState.LOADING );

        final int count = this.parameters.getEntries ();
        this.values = new ValueBuffer[count];

        final long diff = this.end - this.start;

        final double step = (double)diff / (double)count;
        for ( int i = 0; i < count; i++ )
        {
            final long pos = this.start + (long) ( step * i );
            final long endPos = this.start + (long) ( step * ( i + 1 ) );
            this.values[i] = new ValueBuffer ( this.listener, i, pos, endPos );
        }

        if ( count > 0 )
        {
            final long istep = Math.max ( 1, 100000 / count );
            for ( long i = this.start; i < Math.min ( this.end, System.currentTimeMillis () ); i += istep )
            {
                pushData ( i, getValue ( i ) );
            }
        }

        // Send all buffers
        for ( final ValueBuffer buffer : this.values )
        {
            buffer.sendData ();
        }

        this.listener.updateState ( QueryState.COMPLETE );
    }

    public void close ()
    {
        logger.info ( "Close query" );
        this.listener.updateState ( QueryState.DISCONNECTED );
        this.item.remove ( this );
    }

    public void changeParameters ( final QueryParameters parameters )
    {
        this.parameters = parameters;

        this.start = parameters.getStartTimestamp ().getTimeInMillis ();
        this.end = parameters.getEndTimestamp ().getTimeInMillis ();
        this.count = parameters.getEntries ();

        initData ();
    }

    public void tick ( final long tick )
    {
        pushData ( tick, getValue ( tick ) );
        sendBuffer ( tick );
    }

    private double getValue ( final long tick )
    {
        return Math.sin ( tick / 100000.0 ) * 100.0 + random.nextDouble () * 10.0;
    }

    private Integer getBufferIndex ( final long tick )
    {
        if ( this.count <= 0 )
        {
            return null;
        }
        if ( tick < this.start || tick > this.end )
        {
            return null;
        }

        final long bufferSize = ( this.end - this.start ) / this.count;

        long idx = tick - this.start;
        idx = idx / bufferSize;

        if ( idx < 0 || idx >= this.count )
        {
            logger.warn ( "Out of range! {} -> {}", new Object[] { tick, idx } );
            return null;
        }

        return Integer.valueOf ( (int)idx );
    }

    private void sendBuffer ( final long tick )
    {
        final Integer bufferIndex = getBufferIndex ( tick );
        if ( bufferIndex != null )
        {
            this.values[bufferIndex].sendData ();
        }
    }

    private void pushData ( final long tick, final double value )
    {
        final Integer bufferIndex = getBufferIndex ( tick );
        if ( bufferIndex != null )
        {
            this.values[bufferIndex].pushData ( value );
        }
    }
}
