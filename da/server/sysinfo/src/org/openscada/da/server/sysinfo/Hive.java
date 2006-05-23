package org.openscada.da.server.sysinfo;


import java.io.File;

import org.openscada.da.core.common.impl.HiveCommon;
import org.openscada.da.server.sysinfo.items.LoadAverageJob;
import org.openscada.da.server.sysinfo.items.PlainFileDataItem;
import org.openscada.da.server.sysinfo.items.TimeDataItem;
import org.openscada.utils.timing.Scheduler;

public class Hive extends HiveCommon {
	
	private Scheduler _scheduler = new Scheduler();
	
	public Hive ()
	{
		super();
        
		registerItem ( new TimeDataItem("time", _scheduler) );
		registerItem ( new PlainFileDataItem("hostname",new File("/proc/sys/kernel/hostname"), _scheduler, 1000 * 10 ) );
		
		_scheduler.addJob ( new LoadAverageJob(this), 1000 );
	}
}
