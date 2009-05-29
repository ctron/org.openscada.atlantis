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
import java.util.Observable;
import java.util.Observer;

import org.openscada.core.Variant;
import org.openscada.da.client.DataItem;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.ItemManager;

/**
 * Sample showing how to subscribe for events using the {@link DataItem} class.
 * <p>
 * The example shows how to create a new connection, connect, and listen for events
 * coming using the {@link DataItem} class which simplifies some parts of receiving
 * events from OpenSCADA. The DataItem class performs all the merging and cache value
 * handling for us. It extends the common {@link Observable} class and therefore
 * simplifies that handling a little bit. You do not get the
 * much more detailed event information since you only get the information
 * <q>object changed</q>. On the other side you have an instance which always
 * has the latest value merged up for you. So you can access it using {@link DataItem#getValue()},
 * {@link DataItem#getAttributes()} and {@link DataItem#getSubscriptionState()} at any
 * time.
 * </p> 
 * <p>
 * We will listen to the <q>time</q> data item of the test server. The item
 * is an input item and will provided the current unix timestamp every second.
 * </p>
 * 
 * @author Jens Reimann <jens.reimann@inavare.net>
 */
public class Sample5 extends SampleBase implements Observer
{
    private ItemManager itemManager;

    private DataItem dataItem;

    public Sample5 ( final String uri, final String className ) throws Exception
    {
        super ( uri, className );
    }

    @Override
    public void connect () throws Exception
    {
        super.connect ();
        this.itemManager = new ItemManager ( this.connection );
    }

    public void subscribe ()
    {
        // we use a DataItem here which does all the update handling for
        // us and can be accessed using the Observable interface
        this.dataItem = new DataItem ( "time", this.itemManager );
        this.dataItem.addObserver ( this );
    }

    public void unsubscribe ()
    {
        // now remove the update listener
        this.dataItem.deleteObservers ();
        this.dataItem.unregister ();
    }

    public void update ( final Observable o, final Object arg )
    {
        // you can either use the dataItem field or the observable
        // passed on the method call
        final DataItemValue value = this.dataItem.getSnapshotValue ();

        System.out.println ( "Item: " + this.dataItem.getItemId () );
        System.out.println ( "Subscription state: " + value.getSubscriptionState ().name () );
        System.out.println ( "Value of item changed: " + value.getValue ().toString () );
        System.out.println ( String.format ( "Attributes for item: " ) );
        for ( final Map.Entry<String, Variant> entry : value.getAttributes ().entrySet () )
        {
            System.out.println ( String.format ( "'%s' => '%s'", entry.getKey (), entry.getValue ().toString () ) );
        }
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

        Sample5 s = null;
        try
        {
            s = new Sample5 ( uri, className );
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
    }
}
