/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
 * @author Jens Reimann <jens.reimann@th4-systems.com>
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
