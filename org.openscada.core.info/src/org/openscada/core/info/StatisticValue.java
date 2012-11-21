/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.core.info;

import org.openscada.utils.lang.Immutable;

@Immutable
public class StatisticValue
{
    private final Number minimum;

    private final Number maximum;

    private final Number current;

    public StatisticValue ( final Number minimum, final Number maximum, final Number current )
    {
        super ();
        this.minimum = minimum;
        this.maximum = maximum;
        this.current = current;
    }

    public Number getMinimum ()
    {
        return this.minimum;
    }

    public Number getMaximum ()
    {
        return this.maximum;
    }

    public Number getCurrent ()
    {
        return this.current;
    }

    @Override
    public String toString ()
    {
        return String.format ( "%s [%s-%s]", this.current, this.minimum, this.maximum );
    }

}
