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

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.core.client.ConnectionInformation;
import org.openscada.da.client.Connection;
import org.openscada.da.client.ItemUpdateListener;

/**
 * Sample showing how to subscribe for events only
 * <br>
 * The example shows how to create a new connection, connect, and listen for events coming
 * in for a period of 10 seconds.
 * <br>
 * We will listen to the <em>time</em> data item of the test server. The item is an input
 * item and will provided the current unix timestamp every second.
 * 
 * @author Jens Reimann <jens.reimann@inavare.net>
 */
public class Sample1 implements ItemUpdateListener
{
    public Sample1 () throws Exception
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
        
        // add us as item update listener
        // since we subscribe with "initial=true" we will get the current value
        // before any other event. Setting to "false" would ignore the current
        // value of this item and wait for the first change.
        c.addItemUpdateListener ( "time", true, this );
    }
    
    public static void main ( String[] args ) throws Exception
    {
        new Sample1 ();
        // Sleep a little bit to get some events
        Thread.sleep ( 10 * 1000 );
    }

    public void notifyAttributeChange ( Map<String, Variant> attributes, boolean initial )
    {
        // Attributes have changed
        // If it is an "initial" transmission it is a complete set. Otherwise it is only
        // the set of changed attributes.
        System.out.println ( String.format ( "Attributes changed for item: %d update(s)%s", attributes.size (), ( initial ? " (cache-read)" : "" ) ) );
        for ( Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            System.out.println ( String.format ( "'%s' => '%s'", entry.getKey (), entry.getValue ().toString () ) );
        }
    }

    public void notifyValueChange ( Variant value, boolean initial )
    {
        // The value has changed
        // If it is an initial transmission it is not a change but the last change that occurred.
        System.out.println ( "Value of item changed: " + value.toString () + ( initial ? " (cache read)" : "" ) );
    }
}
