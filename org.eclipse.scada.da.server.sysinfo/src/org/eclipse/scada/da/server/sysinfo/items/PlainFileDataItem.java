/*
 * This file is part of the OpenSCADA project
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

package org.eclipse.scada.da.server.sysinfo.items;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.server.common.AttributeMode;
import org.eclipse.scada.da.server.sysinfo.utils.FileUtils;
import org.eclipse.scada.utils.collection.MapBuilder;

public class PlainFileDataItem extends ScheduledDataItem
{

    private final File file;

    public PlainFileDataItem ( final String name, final File file, final ScheduledExecutorService scheduler, final int period )
    {
        super ( name, scheduler, period );
        this.file = file;
    }

    @Override
    public void run ()
    {
        try
        {
            read ();
            updateData ( null, new MapBuilder<String, Variant> ().put ( "error-message", Variant.NULL ).getMap (), AttributeMode.UPDATE );
        }
        catch ( final Exception e )
        {
            // handle error
            updateData ( null, new MapBuilder<String, Variant> ().put ( "error-message", Variant.valueOf ( e.getMessage () ) ).getMap (), AttributeMode.UPDATE );
        }

    }

    private void read () throws IOException
    {
        final String[] data = FileUtils.readFile ( this.file );
        updateData ( Variant.valueOf ( data[0] ), null, null );
    }

}
