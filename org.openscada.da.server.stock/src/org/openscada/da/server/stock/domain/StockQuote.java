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

package org.openscada.da.server.stock.domain;

import java.util.Calendar;

public class StockQuote
{
    private String _symbol = null;

    private Double _value = null;

    private Calendar _timestamp = null;

    private String _error = null;

    public String getSymbol ()
    {
        return this._symbol;
    }

    public void setSymbol ( final String symbol )
    {
        this._symbol = symbol;
    }

    public Calendar getTimestamp ()
    {
        return this._timestamp;
    }

    public void setTimestamp ( final Calendar timestamp )
    {
        this._timestamp = timestamp;
    }

    public Double getValue ()
    {
        return this._value;
    }

    public void setValue ( final Double value )
    {
        this._value = value;
    }

    public String getError ()
    {
        return this._error;
    }

    public void setError ( final String error )
    {
        this._error = error;
    }
}
