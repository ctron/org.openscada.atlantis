/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.client.samples;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.data.SubscriptionState;
import org.openscada.da.client.ItemManager;
import org.openscada.da.client.ItemManagerImpl;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.client.net.Connection;

public class GCTest1 extends SampleBase implements ItemUpdateListener
{
    private ItemManager itemManager;

    private String itemName = "time";

    public GCTest1 ( final String uri, final String className, final String itemName ) throws Exception
    {
        super ( uri, className );
        this.itemName = itemName;
    }

    @Override
    public void connect () throws Exception
    {
        super.connect ();
        this.itemManager = new ItemManagerImpl ( this.connection );
    }

    public void subscribe ()
    {
        // add us as item update listener
        // since we subscribe with "initial=true" we will get the current value
        // before any other event. Setting to "false" would ignore the current
        // value of this item and wait for the first change.
        this.itemManager.addItemUpdateListener ( this.itemName, this );
    }

    public void unsubscribe ()
    {
        // now remove the update listener
        this.itemManager.removeItemUpdateListener ( this.itemName, this );
    }

    @Override
    public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        if ( value != null )
        {
            // The value has changed
            // If it is an initial transmission it is not a change but the last change that occurred.
            System.out.println ( "Value of item changed: " + value.toString () + ( cache ? " (cache read)" : "" ) );
        }

        if ( attributes != null )
        {
            // Attributes have changed
            // If it is an "initial" transmission it is a complete set. Otherwise it is only
            // the set of changed attributes.
            System.out.println ( String.format ( "Attributes changed for item: %d update(s)%s", attributes.size (), cache ? " (cache read)" : "" ) );
            for ( final Map.Entry<String, Variant> entry : attributes.entrySet () )
            {
                System.out.println ( String.format ( "'%s' => '%s'", entry.getKey (), entry.getValue ().toString () ) );
            }
        }
    }

    @Override
    public void notifySubscriptionChange ( final SubscriptionState state, final Throwable subscriptionError )
    {
        System.out.println ( "Subscription state: " + state.name () + " Error: " + ( subscriptionError == null ? "<none>" : subscriptionError.getMessage () ) );
    }

    @Override
    protected void finalize () throws Throwable
    {
        System.out.println ( "Finalized GCTest1" );
        super.finalize ();
    }

    public static void main ( final String[] args ) throws Exception
    {
        final String uri = "da:net://localhost:1202";
        final String className = Connection.class.getName ();

        /*
        if ( args.length > 0 )
            uri = args[0];
        if ( args.length > 1 )
            className = args[1];
        String itemName = "time";
        if ( args.length > 2 )
            itemName = args[2];
            */
        final String itemName = "TW1.state";

        GCTest1 s = null;
        try
        {
            s = new GCTest1 ( uri, className, itemName );
            s.connect ();
            s.subscribe ();
            Thread.sleep ( 10 * 1000 );
            s.unsubscribe ();
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
        s = null;

        Thread.sleep ( 2 * 1000 );
        System.gc ();
        Thread.sleep ( 30 * 1000 );
    }
}
