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

import org.openscada.core.OperationException;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.DataItemEntry;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.FolderEntry;

/**
 * Sample showing how to browse once
 * <br> 
 * @author Jens Reimann <jens.reimann@inavare.net>
 */
public class Sample3 extends SampleBase
{
    public Sample3 ( final String uri, final String className ) throws Exception
    {
        super ( uri, className );
    }

    /**
     * Show one folder entry. 
     * @param entry A folder entry which can be an item or a sub-folder
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

    /**
     * browse once through a predefined folder named "test"
     * @throws InterruptedException
     * @throws OperationException
     */
    public void run () throws InterruptedException, OperationException
    {
        try
        {
            for ( final Entry entry : this.connection.browse ( new Location ( "test" ) ) )
            {
                showEntry ( entry );
            }
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
                s.disconnect ();
            }
        }
    }
}
