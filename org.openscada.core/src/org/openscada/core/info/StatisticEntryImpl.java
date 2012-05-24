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

import java.util.concurrent.atomic.AtomicReference;

public class StatisticEntryImpl implements StatisticEntry
{
    private volatile String label;

    private final Object key;

    private final AtomicReference<StatisticValue> value = new AtomicReference<StatisticValue> ();

    public StatisticEntryImpl ( final Object key )
    {
        this.key = key;
    }

    @Override
    public Object getKey ()
    {
        return this.key;
    }

    public void setLabel ( final String label )
    {
        this.label = label;
    }

    @Override
    public String getLabel ()
    {
        return this.label;
    }

    @Override
    public StatisticValue getValue ()
    {
        return this.value.get ();
    }

    public void setCurrentValue ( final Object key, final double number )
    {
        StatisticValue current;
        StatisticValue newValue;
        do
        {
            current = this.value.get ();

            final Number newMin;
            final Number newMax;
            if ( current != null )
            {
                newMin = min ( current.getMinimum (), number );
                newMax = max ( current.getMaximum (), number );
            }
            else
            {
                newMax = newMin = number;
            }

            newValue = new StatisticValue ( newMin, newMax, number );
        } while ( !this.value.compareAndSet ( current, newValue ) );
    }

    public void changeCurrentValue ( final Object key, final double offset )
    {
        StatisticValue current;
        StatisticValue newValue;
        do
        {
            current = this.value.get ();

            final Number number;
            if ( current == null || current.getCurrent () == null )
            {
                number = offset;
            }
            else
            {
                number = current.getCurrent ().doubleValue () + offset;
            }

            final Number newMin;
            final Number newMax;
            if ( current != null )
            {
                newMin = min ( current.getMinimum (), number );
                newMax = max ( current.getMaximum (), number );
            }
            else
            {
                newMax = newMin = number;
            }

            newValue = new StatisticValue ( newMin, newMax, number );
        } while ( !this.value.compareAndSet ( current, newValue ) );
    }

    private static Number min ( final Number n1, final Number n2 )
    {
        if ( n1 == null )
        {
            return n2;
        }
        if ( n2 == null )
        {
            return n1;
        }
        return Math.min ( n1.doubleValue (), n2.doubleValue () );
    }

    private static Number max ( final Number n1, final Number n2 )
    {
        if ( n1 == null )
        {
            return n2;
        }
        if ( n2 == null )
        {
            return n1;
        }
        return Math.max ( n1.doubleValue (), n2.doubleValue () );
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.key == null ? 0 : this.key.hashCode () );
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
        final StatisticEntryImpl other = (StatisticEntryImpl)obj;
        if ( this.key == null )
        {
            if ( other.key != null )
            {
                return false;
            }
        }
        else if ( !this.key.equals ( other.key ) )
        {
            return false;
        }
        return true;
    }

}
