/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.spring.client.value;

import java.util.Calendar;

import org.openscada.utils.lang.Immutable;

@Immutable
public class Value<T>
{
    private final T value;

    private final boolean manual;

    private final boolean alarm;

    private final Calendar timestamp;

    public Value ( final T value, final boolean manual, final boolean alarm, final Calendar timestamp )
    {
        this.value = value;
        this.manual = manual;
        this.alarm = alarm;
        if ( timestamp != null )
        {
            this.timestamp = (Calendar)timestamp.clone ();
        }
        else
        {
            this.timestamp = null;
        }
    }

    public Value ( final T value, final Value<?> valueProperties )
    {
        this ( value, valueProperties.isManual (), valueProperties.isAlarm (), valueProperties.getTimestamp () );
    }

    public T getValue ()
    {
        return this.value;
    }

    public boolean isManual ()
    {
        return this.manual;
    }

    public boolean isAlarm ()
    {
        return this.alarm;
    }

    public Calendar getTimestamp ()
    {
        return this.timestamp;
    }

    /**
     * Return a <q>default value</q> with not flags and current timestamp
     * @param <T> the type of the value
     * @param defaultValue the value 
     * @return the newly created value, returns never <code>null</code>
     */
    public static <T> Value<T> createDefault ( final T defaultValue )
    {
        return new Value<T> ( defaultValue, false, false, Calendar.getInstance () );
    }

}
