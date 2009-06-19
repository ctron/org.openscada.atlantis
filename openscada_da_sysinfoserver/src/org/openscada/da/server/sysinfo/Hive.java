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
