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

package org.openscada.da.server.sysinfo.items;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.utils.collection.MapBuilder;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.DataItemInputCommon;
import org.openscada.da.server.sysinfo.Hive;
import org.openscada.da.server.sysinfo.utils.FileUtils;

public class LoadAverageJob implements Runnable
{

    private final File file = new File ( "/proc/loadavg" );

    private final DataItemInputCommon avg1 = new DataItemInputCommon ( "loadavg1" );

    private final DataItemInputCommon avg5 = new DataItemInputCommon ( "loadavg5" );

    private final DataItemInputCommon avg15 = new DataItemInputCommon ( "loadavg15" );

    private Hive hive = null;

    private FolderCommon folder = null;

    public LoadAverageJob ( final Hive hive, final FolderCommon folder )
    {
        this.hive = hive;
        this.folder = folder;

        this.hive.registerItem ( this.avg1 );
        this.folder.add ( "1", this.avg1, new MapBuilder<String, Variant> ().put ( "description", Variant.valueOf ( "The 1 minute load avarage" ) ).getMap () );

        this.hive.registerItem ( this.avg5 );
        this.folder.add ( "5", this.avg5, new MapBuilder<String, Variant> ().put ( "description", Variant.valueOf ( "The 5 minute load avarage" ) ).getMap () );

        this.hive.registerItem ( this.avg15 );
        this.folder.add ( "15", this.avg15, new MapBuilder<String, Variant> ().put ( "description", Variant.valueOf ( "The 15 minute load avarage" ) ).getMap () );
    }

    @Override
    public void run ()
    {
        try
        {
            read ();
        }
        catch ( final Exception e )
        {
            // handle error
        }
    }

    private void read () throws IOException
    {
        final String[] data = FileUtils.readFile ( this.file );

        final StringTokenizer tok = new StringTokenizer ( data[0] );

        this.avg1.updateData ( Variant.valueOf ( Double.parseDouble ( tok.nextToken () ) ), null, null );
        this.avg5.updateData ( Variant.valueOf ( Double.parseDouble ( tok.nextToken () ) ), null, null );
        this.avg15.updateData ( Variant.valueOf ( Double.parseDouble ( tok.nextToken () ) ), null, null );
    }
}
