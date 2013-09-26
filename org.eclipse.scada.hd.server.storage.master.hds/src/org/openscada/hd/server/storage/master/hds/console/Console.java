/*
 * This file is part of the openSCADA project
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

package org.openscada.hd.server.storage.master.hds.console;

import java.io.File;

import org.apache.felix.service.command.Descriptor;
import org.openscada.hd.server.storage.hds.StorageConfiguration;
import org.openscada.hd.server.storage.hds.StorageInformation;
import org.openscada.hd.server.storage.master.hds.StorageManager;

public class Console
{
    private final StorageManager manager;

    public Console ( final StorageManager manager )
    {
        this.manager = manager;
    }

    @Descriptor ( "Delete all data outside of the valid time spec" )
    public void purgeAll ()
    {
        System.out.print ( "Purging..." );
        System.out.flush ();

        this.manager.purgeAll ();
        System.out.println ( "done!" );
    }

    @Descriptor ( "List all HDS storages" )
    public void list ()
    {
        System.out.println ( "ID\t\tLocation" );
        System.out.println ( "============================================" );
        for ( final StorageInformation info : this.manager.list () )
        {
            printStorage ( info );
        }
        System.out.println ( "============================================" );
    }

    private void printStorage ( final StorageInformation info )
    {
        System.out.println ( String.format ( "%s\t\t%s", info.getId (), info.getFile () ) );
        final StorageConfiguration cfg = info.getConfiguration ();
        System.out.println ( String.format ( "\t\tnative: %s ms, %s", cfg.getTimeSlice (), cfg.getCount () ) );
    }

    @Descriptor ( "Create a new HDS storage" )
    public void create ( @Descriptor ( "the new id" ) final String id, @Descriptor ( "number of milliseconds each file is valid for" ) final long time, @Descriptor ( "the number of files to keep" ) final int count ) throws Exception
    {
        this.manager.addStorage ( id, time, count );
    }

    @Descriptor ( "Remove storage by id" )
    public void remove ( final String[] args )
    {
        final String usage = "Usage: remove [--force] id1 [id2 [id3]]";
        if ( args.length <= 0 )
        {
            System.out.println ( usage );
            return;
        }

        boolean force = false;
        for ( final String id : args )
        {
            if ( "--force".equals ( id ) )
            {
                force = true;
            }
            else
            {
                System.out.println ( String.format ( "Removing storage '%s' ...", id ) );
                System.out.flush ();
                removeById ( id, force );
                System.out.println ( String.format ( "Removing storage '%s' ... done!", id ) );
            }
        }
    }

    private void removeById ( final String id, final boolean force )
    {
        for ( final StorageInformation info : this.manager.list () )
        {
            if ( id.equals ( info.getId () ) )
            {
                System.out.println ( String.format ( "Removing file - id: %s, file: %s, force: %s", id, info.getFile (), force ) );
                removeByFile ( info.getFile (), force );
            }
        }
    }

    private void removeByFile ( final File file, final boolean force )
    {
        try
        {
            this.manager.removeStorage ( file, force );
        }
        catch ( final Exception e )
        {
            e.printStackTrace ();
        }
    }

}
