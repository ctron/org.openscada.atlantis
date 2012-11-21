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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StatisticsImpl
{
    private final Map<Object, StatisticEntryImpl> entries = new HashMap<Object, StatisticEntryImpl> ();

    public synchronized Collection<StatisticEntry> getEntries ()
    {
        return new ArrayList<StatisticEntry> ( this.entries.values () );
    }

    public void setLabel ( final Object key, final String label )
    {
        getEntry ( key ).setLabel ( label );
    }

    private synchronized StatisticEntryImpl getEntry ( final Object key )
    {
        StatisticEntryImpl result = this.entries.get ( key );
        if ( result != null )
        {
            return result;
        }
        else
        {
            result = new StatisticEntryImpl ( key );
            this.entries.put ( key, result );
            return result;
        }
    }

    public void setCurrentValue ( final Object key, final double number )
    {
        getEntry ( key ).setCurrentValue ( key, number );
    }

    public void changeCurrentValue ( final Object key, final double offset )
    {
        getEntry ( key ).changeCurrentValue ( key, offset );
    }
}
