/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.sysinfo.items;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.core.Variant;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.sysinfo.utils.FileUtils;
import org.openscada.utils.collection.MapBuilder;

public class PlainFileDataItem extends ScheduledDataItem
{

    private final File _file;

    public PlainFileDataItem ( final String name, final File file, final ScheduledExecutorService scheduler, final int period )
    {
        super ( name, scheduler, period );
        this._file = file;
    }

    public void run ()
    {
        try
        {
            read ();
            updateData ( null, new MapBuilder<String, Variant> ().put ( "error-message", new Variant () ).getMap (), AttributeMode.UPDATE );
        }
        catch ( final Exception e )
        {
            // handle error
            updateData ( null, new MapBuilder<String, Variant> ().put ( "error-message", new Variant ( e.getMessage () ) ).getMap (), AttributeMode.UPDATE );
        }

    }

    private void read () throws IOException
    {
        final String[] data = FileUtils.readFile ( this._file );
        updateData ( new Variant ( data[0] ), null, null );
    }

}
