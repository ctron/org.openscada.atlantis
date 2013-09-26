/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.client.samples;

import java.util.Map;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.data.SubscriptionState;
import org.openscada.da.client.ItemManager;
import org.openscada.da.client.ItemManagerImpl;
import org.openscada.da.client.ItemUpdateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample showing how to subscribe for events only <br>
 * The example shows how to create a new connection, connect, and listen for
 * events coming
 * in for a period of 10 seconds. <br>
 * We will listen to the <em>time</em> data item of the test server. The item is
 * an input
 * item and will provided the current unix timestamp every second.
 * 
 * @author Jens Reimann <jens.reimann@th4-systems.com>
 */
public class Sample1 extends SampleBase implements ItemUpdateListener
{

    private final static Logger logger = LoggerFactory.getLogger ( Sample1.class );

    private ItemManager itemManager;

    private final String itemName;

    public Sample1 ( final String uri, final String className, final String itemName ) throws Exception
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

    public void notifyAttributeChange ( final Map<String, Variant> attributes, final boolean initial )
    {
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
        logger.info ( "Finalized" );
        super.finalize ();
    }

    public static void main ( final String[] args ) throws Exception
    {
        String uri = null;
        String className = null;
        String itemName = "time";

        if ( args.length > 0 )
        {
            uri = args[0];
        }
        if ( args.length > 1 )
        {
            className = args[1];
        }
        if ( args.length > 2 )
        {
            itemName = args[2];
        }

        Sample1 s = null;
        try
        {
            s = new Sample1 ( uri, className, itemName );
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
                s.dispose ();
            }
        }
    }
}
