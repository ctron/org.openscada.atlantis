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

// for calculation use algorithm found at 
// http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Weighted_incremental_algorithm
//
// which is
//
// def weighted_incremental_variance(dataWeightPairs):
//     sumweight = 0
//     mean = 0
//     M2 = 0
// 
//     for x, weight in dataWeightPairs:
//         temp = weight + sumweight
//         delta = x − mean
//         R = delta * weight / temp
//         mean = mean + R
//         M2 = M2 + sumweight * delta * R
//         sumweight = temp
//  
//     variance_n = M2/sumweight
//     variance = variance_n * len(dataWeightPairs)/(len(dataWeightPairs) − 1)
//
// in this case we use the variable numOfIncrements for the expression len(dataWeightPairs)
// which has to be updated on every value change

public class RunningAverage
{
    private long firstTimestamp;

    private double lastValue = Double.NaN;

    private long lastTimestamp;

    private final MathContext mathContext = new MathContext ( 10, RoundingMode.HALF_DOWN );

    private BigDecimal M2 = BigDecimal.ZERO;

    private BigDecimal mean = BigDecimal.ZERO;

    private long sumWeight = 0;

    private long numOfIncrements = 0;

    private boolean hadValue = false;

    public void next ( final double value, final long timestamp )
    {
        increment ( timestamp );

        this.lastValue = value;
        this.lastTimestamp = timestamp;

        this.hadValue = this.hadValue || ( !Double.isNaN ( value ) );
    }

    private void increment ( final long timestamp )
    {
        final long offset = timestamp - this.lastTimestamp;
        final long newSumWeight = offset + this.sumWeight;

        if ( offset > 0 )
        {
            if ( !Double.isNaN ( this.lastValue ) )
            {
                final BigDecimal delta = BigDecimal.valueOf ( this.lastValue ).subtract ( this.mean );
                final BigDecimal R = delta.multiply ( BigDecimal.valueOf ( offset ) ).divide ( BigDecimal.valueOf ( newSumWeight ), this.mathContext );
                this.mean = this.mean.add ( R );
                this.M2 = this.M2.add ( BigDecimal.valueOf ( this.sumWeight ).multiply ( delta ).multiply ( R ) );
            }
            this.numOfIncrements += 1;
            this.sumWeight = newSumWeight;
        }
    }

    public void step ( final long timestamp )
    {
        this.firstTimestamp = timestamp;
        this.lastTimestamp = timestamp;

        this.M2 = BigDecimal.ZERO;
        this.mean = BigDecimal.ZERO;
        this.sumWeight = 0;
        this.numOfIncrements = 0;
        this.hadValue = false;
    }

    public double getAverage ( final long lastTimestamp )
    {
        increment ( lastTimestamp );
        if ( ( lastTimestamp == this.firstTimestamp ) || !this.hadValue )
        {
            return Double.NaN;
        }
        else
        {
            return this.mean.doubleValue ();
        }
    }

    public double getDeviation ( final long lastTimestamp )
    {
        increment ( lastTimestamp );
        if ( ( lastTimestamp == this.firstTimestamp ) || !this.hadValue )
        {
            return Double.NaN;
        }
        else
        {
            final BigDecimal variance_n = this.M2.divide ( BigDecimal.valueOf ( this.sumWeight ), this.mathContext );
            final BigDecimal variance = variance_n.multiply ( BigDecimal.valueOf ( this.numOfIncrements ).divide ( BigDecimal.valueOf ( this.numOfIncrements - 1 ), this.mathContext ) );
            return Math.sqrt ( variance.doubleValue () );
        }
    }
}