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
    private static Logger _log = Logger.getLogger ( CounterValue.class );

    private long _total = 0;

    private long _lastTickValue = 0;

    private long _lastTimestamp = 0;

    private CounterOutput _output;

    public synchronized void add ( final long value )
    {
        this._total += value;
        this._lastTickValue = this._lastTickValue + Math.abs ( value );
        _log.debug ( String.format ( "Adding: %s, LastTickValue: %s", value, this._lastTickValue ) );
    }

    public synchronized void tick ()
    {
        // get now
        final long ts = System.currentTimeMillis ();

        // get the difference ( in seconds )
        long diff = ( ts - this._lastTimestamp ) / 1000;
        this._lastTimestamp = ts;

        // just in case
        if ( diff == 0 )
        {
            diff = 1;
        }

        // we need to do this here ... since otherwise the update call later will
        // increment the counter and setting the tickValue to null will discard
        // this information
        final long lastTickValue = this._lastTickValue;
        this._lastTickValue = 0;

        // calculate the average
        final double avg = (double)lastTickValue / (double)diff;
        _log.debug ( String.format ( "LastTickValue: %s, Diff: %s, Avg: %s", lastTickValue, diff, avg ) );
        this._output.setTickValue ( avg, this._total );
    }

    public void setOutput ( final CounterOutput output )
    {
        this._output = output;
    }
}
