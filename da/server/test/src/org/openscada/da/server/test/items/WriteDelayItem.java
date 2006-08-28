/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.test.items;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.core.NotConvertableException;
import org.openscada.da.core.NullValueException;
import org.openscada.da.core.Variant;
import org.openscada.da.core.common.DataItemOutput;
import org.openscada.da.core.common.WriteAttributesHelper;
import org.openscada.da.core.server.InvalidOperationException;
import org.openscada.da.core.server.WriteAttributesOperationListener.Results;

public class WriteDelayItem extends DataItemOutput
{

    public WriteDelayItem ( String name )
    {
        super ( name );
    }

    public Map<String, Variant> getAttributes ()
    {
        return new HashMap<String, Variant>();
    }

    public Results setAttributes ( Map<String, Variant> attributes )
    {
        return WriteAttributesHelper.errorUnhandled ( null, attributes );
    }

    public void setValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
       int delay = value.asInteger ();
       
       System.out.println ( "Start write: " + delay + "ms" );
       try
       {
           Thread.sleep ( delay );
           System.out.println ( "End write" );
       }
       catch ( InterruptedException e )
       {
           System.err.println ( "Write failed" );
           e.printStackTrace();
       }
       
    }

}
