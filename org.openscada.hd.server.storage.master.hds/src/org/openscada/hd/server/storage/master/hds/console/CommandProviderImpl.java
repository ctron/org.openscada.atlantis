/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.openscada.hd.server.storage.hds.StorageConfiguration;
import org.openscada.hd.server.storage.hds.StorageInformation;
import org.openscada.hd.server.storage.master.hds.StorageManager;

public class CommandProviderImpl implements CommandProvider
{
    private final StorageManager manager;

    public CommandProviderImpl ( final StorageManager manager )
    {
        this.manager = manager;
    }

    @Override
    public String getHelp ()
    {
        final StringWriter sw = new StringWriter ();
        final PrintWriter pw = new PrintWriter ( sw );

        pw.println ( "---HDS Storage Manager---" );
        pw.println ( "\taddHDStorage <id> <timeSlice in ms> <numSlices>" );
        pw.println ( "\tremoveHDStorage <id> [-force]" );
        pw.println ( "\tremoveHDStorageByFile <file> [-force]" );
        pw.println ( "\tlistHDStorages" );
        pw.println ( "\tpurgeHDStorages" );

        pw.close ();

        return sw.toString ();
    }

    public void _removeHDStorageByFile ( final CommandInterpreter intr )
    {
        remove ( intr, false );
    }

    public void _removeHDStorage ( final CommandInterpreter intr )
    {
        remove ( intr, true );
    }

    public void remove ( final CommandInterpreter intr, final boolean byId )
    {
        final String id = intr.nextArgument ();
        if ( id == null )
        {
            intr.println ( "removeHDStorage <id> [-force]" );
            return;
        }

        final String forceString = intr.nextArgument ();

        boolean force = false;
        if ( forceString != null )
        {
            force = "-force".equals ( forceString );
        }

        if ( byId )
        {
            removeById ( intr, id, force );
        }
        else
        {
            intr.println ( String.format ( "Removing storage - file: %s, force: %s", id, force ) );
            removeByFile ( intr, new File ( id ), force );
        }
    }

    private void removeByFile ( final CommandInterpreter intr, final File file, final boolean force )
    {
        try
        {
            this.manager.removeStorage ( file, force );
        }
        catch ( final Exception e )
        {
            intr.printStackTrace ( e );
        }
    }

    private void removeById ( final CommandInterpreter intr, final String id, final boolean force )
    {
        for ( final StorageInformation info : this.manager.list () )
        {
            if ( id.equals ( info.getId () ) )
            {
                intr.println ( String.format ( "Removing storage - id: %s, file: %s, force: %s", id, info.getFile (), force ) );
                removeByFile ( intr, info.getFile (), force );
                return;
            }
        }
    }

    public void _purgeHDStorages ( final CommandInterpreter intr )
    {
        this.manager.purgeAll ();
    }

    public void _listHDStorages ( final CommandInterpreter intr )
    {
        intr.println ( "ID\t\tLocation" );
        intr.println ( "============================================" );
        for ( final StorageInformation info : this.manager.list () )
        {
            printStorage ( intr, info );
        }
        intr.println ( "============================================" );
    }

    private void printStorage ( final CommandInterpreter intr, final StorageInformation info )
    {
        intr.println ( String.format ( "%s\t\t%s", info.getId (), info.getFile () ) );
        final StorageConfiguration cfg = info.getConfiguration ();
        intr.println ( String.format ( "\t\tnative: %s ms, %s", cfg.getTimeSlice (), cfg.getCount () ) );
    }

    public void _addHDStorage ( final CommandInterpreter intr )
    {
        final String id = intr.nextArgument ();
        final String timeStr = intr.nextArgument ();
        final String countStr = intr.nextArgument ();

        if ( id == null || timeStr == null || countStr == null )
        {
            intr.println ( "addHDStorage <storage id> <timeSlice> <numSlices>" );
            return;
        }

        final long time = Long.parseLong ( timeStr );
        final int count = Integer.parseInt ( countStr );

        if ( time <= 0 )
        {
            intr.println ( "<timeSlice> must be greater then zero" );
            return;
        }

        if ( count <= 0 )
        {
            intr.println ( "<numSlices> must be greater then zero" );
            return;
        }

        try
        {
            this.manager.addStorage ( id, time, count );
        }
        catch ( final Exception e )
        {
            intr.println ( String.format ( "Failed to add storage '%s'", id ) );
            intr.printStackTrace ( e );
        }
    }

}
