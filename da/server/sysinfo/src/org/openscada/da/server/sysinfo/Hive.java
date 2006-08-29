/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

import org.openscada.core.Variant;
import org.openscada.da.core.browser.common.FolderCommon;
import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.common.impl.HiveCommon;
import org.openscada.da.server.sysinfo.items.LoadAverageJob;
import org.openscada.da.server.sysinfo.items.PlainFileDataItem;
import org.openscada.da.server.sysinfo.items.TimeDataItem;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.timing.Scheduler;

public class Hive extends HiveCommon {
	
	private Scheduler _scheduler = new Scheduler();
	
	public Hive ()
	{
		super();
        
        FolderCommon rootFolder = new FolderCommon ();
        setRootFolder ( rootFolder );
        
        DataItem item;
		registerItem ( item = new TimeDataItem ( "time", _scheduler ) );
        rootFolder.add ( "time", item, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "Time since the epoc in milliseconds!" ) )
                .getMap ()
        );
        
		registerItem ( item = new PlainFileDataItem ( "hostname", new File ( "/proc/sys/kernel/hostname" ), _scheduler, 1000 * 10 ) );
        rootFolder.add ( "hostname", item, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "Hostname of the computer the server is running on." ) )
                .getMap ()
        );
		
        FolderCommon loadFolder = new FolderCommon ();
        rootFolder.add ( "loadavg", loadFolder, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "Load avarage information" ) )
                .getMap ()
        );
		_scheduler.addJob ( new LoadAverageJob ( this, loadFolder ), 1000 );
        
	}
}
