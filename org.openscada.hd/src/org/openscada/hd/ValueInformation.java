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

@Immutable
public class ValueInformation
{
    /**
     * The percent count (from 0.0 to 1.0) of valid values
     */
    private final double quality;

    private final Calendar startTimestamp;

    private final Calendar endTimestamp;

    /**
     * The number of level 0 entries that where used to generate this value
     */
    private final long sourceValues;

    public ValueInformation ( final Calendar startTimestamp, final Calendar endTimestamp, final double quality, final long sourceValues )
    {
        super ();
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.quality = quality;
        this.sourceValues = sourceValues;
    }

    public double getQuality ()
    {
        return this.quality;
    }

    public Calendar getStartTimestamp ()
    {
        return this.startTimestamp;
    }

    public Calendar getEndTimestamp ()
    {
        return this.endTimestamp;
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

}
