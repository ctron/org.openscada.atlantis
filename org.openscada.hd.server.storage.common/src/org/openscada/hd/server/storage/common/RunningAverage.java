/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
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
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.hd.server.storage.common;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class RunningAverage
{
    private double lastValue = Double.NaN;

    private long lastTimestamp;

    private BigDecimal counter;

    private long firstTimestamp;

    private final MathContext mathContext = new MathContext ( 10, RoundingMode.HALF_DOWN );

    public void next ( final double value, final long timestamp )
    {
        increment ( timestamp );

        this.lastValue = value;
        this.lastTimestamp = timestamp;
    }

    private void increment ( final long timestamp )
    {
        if ( !Double.isNaN ( this.lastValue ) )
        {
            final long offset = timestamp - this.lastTimestamp;

            final BigDecimal localCounter = BigDecimal.valueOf ( offset ).multiply ( BigDecimal.valueOf ( this.lastValue ), this.mathContext );

            if ( this.counter != null )
            {
                this.counter = this.counter.add ( localCounter );
            }
            else
            {
                this.counter = localCounter;
            }
        }
    }

    public void step ( final long timestamp )
    {
        this.firstTimestamp = timestamp;
        this.lastTimestamp = timestamp;
        this.counter = null;
    }

    public double getAverage ( final long lastTimestamp )
    {
        increment ( lastTimestamp );
        if ( this.counter == null )
        {
            return Double.NaN;
        }
        else
        {
            return this.counter.divide ( BigDecimal.valueOf ( lastTimestamp - this.firstTimestamp ), this.mathContext ).doubleValue ();
        }
    }
}