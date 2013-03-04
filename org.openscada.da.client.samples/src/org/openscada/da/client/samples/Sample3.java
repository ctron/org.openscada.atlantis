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

import org.openscada.core.OperationException;
import org.openscada.da.client.BrowseOperationCallback;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.DataItemEntry;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.FolderEntry;

/**
 * Sample showing how to browse once <br>
 * 
 * @author Jens Reimann <jens.reimann@th4-systems.com>
 */
public class Sample3 extends SampleBase
{
    public Sample3 ( final String uri, final String className ) throws Exception
    {
        super ( uri, className );
    }

    /**
     * Show one folder entry.
     * 
     * @param entry
     *            A folder entry which can be an item or a sub-folder
     */
    protected void showEntry ( final Entry entry )
    {
        System.out.print ( "'" + entry.getName () + "' " );
        if ( entry instanceof FolderEntry )
        {
            System.out.print ( "[Folder] " );
        }
        else if ( entry instanceof DataItemEntry )
        {
            System.out.print ( "[Item]: " + ( (DataItemEntry)entry ).getId () );
        }

        System.out.println ();
    }

    protected void dumpEntries ( final Entry[] entries )
    {
        for ( final Entry entry : entries )
        {
            showEntry ( entry );
        }
    }

    /**
     * browse once through a predefined folder named "test"
     * 
     * @throws InterruptedException
     * @throws OperationException
     */
    public void run () throws InterruptedException, OperationException
    {
        try
        {
            this.connection.browse ( new Location ( "test" ), new BrowseOperationCallback () {

                @Override
                public void failed ( final String error )
                {
                    System.err.println ( "Error: " + error );
                }

                @Override
                public void error ( final Throwable e )
                {
                    e.printStackTrace ();
                }

                @Override
                public void complete ( final Entry[] entries )
                {
                    dumpEntries ( entries );
                }
            } );
        }
        catch ( final Exception e )
        {
            e.printStackTrace ();
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

        Sample3 s = null;
        try
        {
            s = new Sample3 ( uri, className );
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
