/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2007 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.spring.tools.csv;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;

public class CSVControllerDataItem extends DataItemInputOutputChained
{
    private CSVDataItem _item;

    private Variant lastValue = new Variant ();

    public CSVControllerDataItem ( CSVDataItem item )
    {
        super ( item.getInformation ().getName () + "#controller" );
        _item = item;
        _item.setController ( this );
    }

    public void handleWrite ( Variant value )
    {
        updateValue ( value );
    }

    @Override
    protected void writeCalculatedValue ( Variant value ) throws NotConvertableException, InvalidOperationException
    {
        _item.updateValue ( value );

        if ( !lastValue.equals ( value ) )
        {
            Map<String, Variant> attributes = new HashMap<String, Variant> ();
            attributes.put ( "timestamp", new Variant ( System.currentTimeMillis () ) );
            _item.updateAttributes ( attributes );
            lastValue = new Variant ( value );
        }
    }
}
