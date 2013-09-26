/*
 * This file is part of the OpenSCADA project
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

package org.eclipse.scada.da.server.sysinfo;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.da.server.sysinfo.items.LoadAverageJob;
import org.eclipse.scada.da.server.sysinfo.items.PlainFileDataItem;
import org.eclipse.scada.da.server.sysinfo.items.TimeDataItem;
import org.eclipse.scada.utils.collection.MapBuilder;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.impl.HiveCommon;

public class Hive extends HiveCommon
{
    private ScheduledExecutorService scheduler;

    private FolderCommon loadFolder;

    private final FolderCommon rootFolder;

    public Hive ()
    {
        super ();

        this.rootFolder = new FolderCommon ();
        setRootFolder ( this.rootFolder );

    }

    @Override
    public String getHiveId ()
    {
        return "org.eclipse.scada.da.server.sysinfo";
    }

    private void createModel ()
    {
        DataItem item;
        registerItem ( item = new TimeDataItem ( "time", this.scheduler ) );
        this.rootFolder.add ( "time", item, new MapBuilder<String, Variant> ().put ( "description", Variant.valueOf ( "Time since the epoc in milliseconds!" ) ).getMap () );

        registerItem ( item = new PlainFileDataItem ( "hostname", new File ( "/proc/sys/kernel/hostname" ), this.scheduler, 1000 * 10 ) );
        this.rootFolder.add ( "hostname", item, new MapBuilder<String, Variant> ().put ( "description", Variant.valueOf ( "Hostname of the computer the server is running on." ) ).getMap () );

        this.loadFolder = new FolderCommon ();
        this.rootFolder.add ( "loadavg", this.loadFolder, new MapBuilder<String, Variant> ().put ( "description", Variant.valueOf ( "Load avarage information" ) ).getMap () );
    }

    @Override
    protected void performStart () throws Exception
    {
        super.performStart ();
        this.scheduler = new ScheduledThreadPoolExecutor ( 1 );

        createModel ();

        this.scheduler.scheduleAtFixedRate ( new LoadAverageJob ( this, this.loadFolder ), 1000, 1000, TimeUnit.MILLISECONDS );
    }

    @Override
    protected void performStop () throws Exception
    {
        this.scheduler.shutdown ();
        super.performStop ();
    }
}
