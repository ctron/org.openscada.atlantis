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

package org.openscada.da.server.sysinfo;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.sysinfo.items.LoadAverageJob;
import org.openscada.da.server.sysinfo.items.PlainFileDataItem;
import org.openscada.da.server.sysinfo.items.TimeDataItem;
import org.openscada.utils.collection.MapBuilder;

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

    private void createModel ()
    {
        DataItem item;
        registerItem ( item = new TimeDataItem ( "time", this.scheduler ) );
        this.rootFolder.add ( "time", item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Time since the epoc in milliseconds!" ) ).getMap () );

        registerItem ( item = new PlainFileDataItem ( "hostname", new File ( "/proc/sys/kernel/hostname" ), this.scheduler, 1000 * 10 ) );
        this.rootFolder.add ( "hostname", item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Hostname of the computer the server is running on." ) ).getMap () );

        this.loadFolder = new FolderCommon ();
        this.rootFolder.add ( "loadavg", this.loadFolder, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Load avarage information" ) ).getMap () );
    }

    @Override
    public void start () throws Exception
    {
        super.start ();
        this.scheduler = new ScheduledThreadPoolExecutor ( 1 );

        createModel ();

        this.scheduler.scheduleAtFixedRate ( new LoadAverageJob ( this, this.loadFolder ), 1000, 1000, TimeUnit.MILLISECONDS );
    }

    @Override
    public void stop () throws Exception
    {
        this.scheduler.shutdown ();
        super.stop ();
    }
}
