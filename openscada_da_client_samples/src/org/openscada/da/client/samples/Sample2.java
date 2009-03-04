/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.samples;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.NoConnectionException;

/**
 * Sample showing how to write to a data item
 * <br>
 * The example shows how to create a new connection, connect, and write the main value
 * and some attributes.
 * <br>
 * Since we are using the <em>test-1</em> item of the test server it is no problem
 * writing any value and any attribute to it. If the item is an "input onyl"
 * item you cannot write the main value. And attributes might be restricted
 * to data item specific attributes that are available.
 * 
 * @author Jens Reimann <jens.reimann@inavare.net>
 */
public class Sample2 extends SampleBase
{
    public Sample2 ( final String uri, final String className ) throws Exception
    {
        super ( uri, className );
    }

    public void run () throws NoConnectionException, OperationException
    {
        // set the main value
        this.connection.write ( "test-1", new Variant ( "Hello World" ) );

        // set some attributes
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "hello", new Variant ( "world" ) );
        attributes.put ( "foo", new Variant ( "bar" ) );
        this.connection.writeAttributes ( "test-1", attributes );
    }

    public static void main ( final String[] args ) throws Exception
    {
        String uri = null;
        String className = null;

        if ( args.length > 0 )
        {
            uri = args[0];
        }
        if ( args.length > 1 )
        {
            className = args[1];
        }

        Sample2 s = null;
        try
        {
            s = new Sample2 ( uri, className );
            s.connect ();
            s.run ();
        }
        catch ( final Throwable e )
        {
            e.printStackTrace ();
        }
        finally
        {
            if ( s != null )
            {
                s.disconnect ();
            }
        }
    }
}
