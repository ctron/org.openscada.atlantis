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

package org.openscada.da.server.test.items;

import java.io.File;
import java.io.IOException;
import java.util.Timer;

import org.openscada.core.Variant;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.test.utils.FileUtils;
import org.openscada.utils.collection.MapBuilder;

public class PlainFileDataItem extends ScheduledDataItem
{

    private final File _file;

    public PlainFileDataItem ( final String name, final File file, final Timer timer, final int period )
    {
        super ( name, timer, period );
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
