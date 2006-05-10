package org.openscada.da.server.sysinfo;


import java.io.File;

import org.openscada.da.core.common.DataItemCommand;
import org.openscada.da.core.common.MemoryDataItem;
import org.openscada.da.core.common.impl.HiveCommon;
import org.openscada.da.core.data.Variant;
import org.openscada.utils.timing.Scheduler;

public class Hive extends HiveCommon {
	
	private Scheduler _scheduler = new Scheduler();
	
	public Hive ()
	{
		super();
		
		registerItem(new MemoryDataItem("test") );
		
		DataItemCommand cmd;
		
		cmd = new DataItemCommand("hello");
		cmd.addListener(new DataItemCommand.Listener(){

			public void command(Variant value) {
				System.out.println ( "Hello World!" );
			}});
		
		registerItem ( cmd );
		
		registerItem ( new TimeDataItem("time", _scheduler) );
		registerItem ( new PlainFileDataItem("hostname",new File("/proc/sys/kernel/hostname"), _scheduler, 1000 * 10 ) );
		registerItem ( new PlainFileDataItem("file",new File("/tmp/da.txt"), _scheduler, 1000 ) );
		
		_scheduler.addJob ( new LoadAverageJob(this), 1000 );
	}
}
