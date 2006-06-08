package org.openscada.da.server.sysinfo;


import java.io.File;

import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.common.impl.FolderCommon;
import org.openscada.da.core.common.impl.HiveCommon;
import org.openscada.da.core.data.Variant;
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
