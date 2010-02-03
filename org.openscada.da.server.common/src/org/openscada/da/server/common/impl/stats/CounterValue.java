/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.server.common.impl.stats;

import org.apache.log4j.Logger;

public class CounterValue implements Tickable
{
    private static Logger log = Logger.getLogger ( CounterValue.class );

    private long total = 0;

    private long lastTickValue = 0;

    private long lastTimestamp = 0;

    private CounterOutput output;

    public synchronized void add ( final long value )
    {
        this.total += value;
        this.lastTickValue = this.lastTickValue + Math.abs ( value );
        log.debug ( String.format ( "Adding: %s, LastTickValue: %s", value, this.lastTickValue ) );
    }

    public synchronized void tick ()
    {
        // get now
        final long ts = System.currentTimeMillis ();

        // get the difference ( in seconds )
        long diff = ( ts - this.lastTimestamp ) / 1000;
        this.lastTimestamp = ts;

        // just in case
        if ( diff == 0 )
        {
            diff = 1;
        }

        // we need to do this here ... since otherwise the update call later will
        // increment the counter and setting the tickValue to null will discard
        // this information
        final long lastTickValue = this.lastTickValue;
        this.lastTickValue = 0;

        // calculate the average
        final double avg = (double)lastTickValue / (double)diff;
        log.debug ( String.format ( "LastTickValue: %s, Diff: %s, Avg: %s", lastTickValue, diff, avg ) );
        this.output.setTickValue ( avg, this.total );
    }

    public void setOutput ( final CounterOutput output )
    {
        this.output = output;
    }
}
