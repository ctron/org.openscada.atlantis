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

import org.openscada.core.Variant;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.core.client.ConnectionInformation;
import org.openscada.da.client.Connection;

/**
 * Sample showing how to write to a data item
 * <br>
 * The example shows how to create a new connection, connect, and write the main value
 * and some attributes.
 * <br>
 * Since we are using the <em>test</em> item of the test server it is no problem
 * writing any value and any attribute to it. If the item is an "input onyl"
 * item you cannot write the main value. And attributes might be restricted
 * to data item specific attributes that are available.
 * 
 * @author Jens Reimann <jens.reimann@inavare.net>
 */
public class Sample2
{
    public Sample2 () throws Exception
    {
        Class.forName ( "org.openscada.da.client.net.Connection" );
        ConnectionInformation ci = ConnectionInformation.fromURI ( "da:net://localhost:1202" );
        
        Connection c = (Connection)ConnectionFactory.create ( ci );
        if ( c == null )
            throw new Exception ( "Unable to find a connection driver for specified URI" );
        
        // trigger the connection
        c.connect ();
        try
        {
            // wait until the connection is established. If it already is the call
            // will return immediately.
            // If the connect attempt fails an exception is thrown.
            c.waitForConnection ();
        }
        catch ( Throwable e )
        {
            // we were unlucky
            throw new Exception ( "Unable to create connection", e );
        }
        
        // set the main value
        c.write ( "test", new Variant ( "Hello World" ) );
        
        // set some attributes
        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "hello", new Variant ( "world" ) );
        attributes.put ( "foo", new Variant ( "bar" ) );
        c.writeAttributes ( "test", attributes );
    }
    
    public static void main ( String[] args ) throws Exception
    {
        new Sample2 ();
    }
}
