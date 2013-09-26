/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.eclipse.scada.da.client.samples;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.scada.core.OperationException;
import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.client.NoConnectionException;

/**
 * Sample showing how to write to a data item <br>
 * The example shows how to create a new connection, connect, and write the main
 * value
 * and some attributes. <br>
 * Since we are using the <em>test-1</em> item of the test server it is no
 * problem
 * writing any value and any attribute to it. If the item is an "input onyl"
 * item you cannot write the main value. And attributes might be restricted
 * to data item specific attributes that are available.
 * 
 * @author Jens Reimann <jens.reimann@th4-systems.com>
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
        this.connection.write ( "test-1", Variant.valueOf ( "Hello World" ), null, null );

        // set some attributes
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "hello", Variant.valueOf ( "world" ) );
        attributes.put ( "foo", Variant.valueOf ( "bar" ) );
        this.connection.writeAttributes ( "test-1", attributes, null, null );
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
                s.dispose ();
            }
        }
    }
}
