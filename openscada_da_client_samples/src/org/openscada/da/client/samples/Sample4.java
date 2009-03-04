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

import java.util.Collection;

import org.openscada.core.OperationException;
import org.openscada.da.client.FolderListener;
import org.openscada.da.client.FolderManager;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.DataItemEntry;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.FolderEntry;

/**
 * Sample showing how to browse using subscriptions
 * <br> 
 * @author Jens Reimann <jens.reimann@inavare.net>
 */
public class Sample4 extends SampleBase
{
    private FolderManager folderManager = null;

    public Sample4 ( final String uri, final String className ) throws Exception
    {
        super ( uri, className );
    }

    @Override
    public void connect () throws Exception
    {
        super.connect ();
        this.folderManager = new FolderManager ( this.connection );
    }

    protected void showEntry ( final Entry entry )
    {
        System.out.print ( "'" + entry.getName () + "' " );
        if ( entry instanceof FolderEntry )
        {
            System.out.print ( "[Folder] " );
        }
        else if ( entry instanceof DataItemEntry )
        {
            System.out.print ( "[Item]" );
        }

        System.out.println ();
    }

    public void subscribe () throws InterruptedException, OperationException
    {
        this.folderManager.addFolderListener ( new FolderListener () {

            public void folderChanged ( final Collection<Entry> added, final Collection<String> removed, final boolean full )
            {
                System.out.println ( String.format ( "Added: %d Removed: %d, Full: %s", added.size (), removed.size (), full ) );
                for ( final Entry entry : added )
                {
                    showEntry ( entry );
                }
                for ( final String entry : removed )
                {
                    System.out.println ( String.format ( "Remove: '%s'", entry ) );
                }
            }
        }, new Location () );
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

        Sample4 s = null;
        try
        {
            s = new Sample4 ( uri, className );
            s.connect ();
            s.subscribe ();
            Thread.sleep ( 5 * 1000 );
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
