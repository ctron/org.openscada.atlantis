/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.hd;

import java.util.Calendar;

import org.openscada.utils.lang.Immutable;

/**
 * An information object for a value range
 * @author Jens Reimann
 *
 */
@Immutable
public final class ValueInformation
{
    /**
     * The percent count (from 0.0 to 1.0) of valid values
     */
    private final double quality;

    /**
     * The percent count (from 0.0 to 1.0) of manual values
     */
    private final double manualPercentage;

    private final Calendar startTimestamp;

    private final Calendar endTimestamp;

    /**
     * The number of level 0 entries that where used to generate this value
     */
    private final long sourceValues;

    public ValueInformation ( final Calendar startTimestamp, final Calendar endTimestamp, final double quality, final double manualPercentage, final long sourceValues )
    {
        super ();
        this.startTimestamp = (Calendar)startTimestamp.clone ();
        this.endTimestamp = (Calendar)endTimestamp.clone ();
        this.quality = quality;
        this.manualPercentage = manualPercentage;
        this.sourceValues = sourceValues;
    }

    /**
     * Get the quality of the value range in percent (0.0 to 1.0)
     * @return the quality
     */
    public double getQuality ()
    {
        return this.quality;
    }

    /**
     * Get the amount of manual values in percent (0.0 to 1.0)
     * <p>
     * <code>0.0</code> means no manual values, <code>1.0</code> means all values where manual 
     * </p>
     * @return the manual percent count
     */
    public double getManualPercentage ()
    {
        return this.manualPercentage;
    }

    public Calendar getStartTimestamp ()
    {
        return (Calendar)this.startTimestamp.clone ();
    }

    public Calendar getEndTimestamp ()
    {
        return (Calendar)this.endTimestamp.clone ();
    }

    public long getSourceValues ()
    {
        return this.sourceValues;
    }

    @Override
    public String toString ()
    {
        return String.format ( "%tc -> %tc (quality: %s, source values: %s)", this.startTimestamp, this.endTimestamp, this.quality, this.sourceValues );
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.endTimestamp == null ? 0 : this.endTimestamp.hashCode () );
        long temp;
        temp = Double.doubleToLongBits ( this.quality );
        result = prime * result + (int) ( temp ^ temp >>> 32 );
        temp = Double.doubleToLongBits ( this.manualPercentage );
        result = prime * result + (int) ( temp ^ temp >>> 32 );
        result = prime * result + (int) ( this.sourceValues ^ this.sourceValues >>> 32 );
        result = prime * result + ( this.startTimestamp == null ? 0 : this.startTimestamp.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass () != obj.getClass () )
        {
            return false;
        }
        final ValueInformation other = (ValueInformation)obj;
        if ( this.endTimestamp == null )
        {
            if ( other.endTimestamp != null )
            {
                return false;
            }
        }
        else if ( !this.endTimestamp.equals ( other.endTimestamp ) )
        {
            return false;
        }
        if ( Double.doubleToLongBits ( this.quality ) != Double.doubleToLongBits ( other.quality ) )
        {
            return false;
        }
        if ( Double.doubleToLongBits ( this.manualPercentage ) != Double.doubleToLongBits ( other.manualPercentage ) )
        {
            return false;
        }
        if ( this.sourceValues != other.sourceValues )
        {
            return false;
        }
        if ( this.startTimestamp == null )
        {
            if ( other.startTimestamp != null )
            {
                return false;
            }
        }
        else if ( !this.startTimestamp.equals ( other.startTimestamp ) )
        {
            return false;
        }
        return true;
    }

}
