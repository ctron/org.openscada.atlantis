/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.hd.server.storage.internal;

import java.util.Map;

import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;

/**
 * This object is used to transport calculated data from one method to the other.
 * @author Ludwig Straub
 */
public class CalculatedData
{
    /** Calculated array of value information objects. */
    private ValueInformation[] valueInformations;

    /** Calculated data. */
    private Map<String, Value[]> data;

    /**
     * Standard constructor.
     */
    public CalculatedData ()
    {
        this ( null, null );
    }

    /**
     * Constructor.
     * @param valueInformations calculated array of value information objects.
     * @param data calculated data
     */
    public CalculatedData ( final ValueInformation[] valueInformations, final Map<String, Value[]> data )
    {
        this.valueInformations = valueInformations;
        this.data = data;
    }

    /**
     * This method returns the calculated array of value information objects.
     * @return calculated array of value information objects
     */
    public ValueInformation[] getValueInformations ()
    {
        return this.valueInformations;
    }

    /**
     * This method sets the calculated array of value information objects.
     * @param valueInformations calculated array of value information objects
     */
    public void setValueInformations ( final ValueInformation[] valueInformations )
    {
        this.valueInformations = valueInformations;
    }

    /**
     * This method returns the map of calculated data.
     * @return map of calculated data
     */
    public Map<String, Value[]> getData ()
    {
        return this.data;
    }

    /**
     * This method sets the map of calculated data.
     * @param data map of calculated data
     */
    public void setData ( final Map<String, Value[]> data )
    {
        this.data = data;
    }
}
