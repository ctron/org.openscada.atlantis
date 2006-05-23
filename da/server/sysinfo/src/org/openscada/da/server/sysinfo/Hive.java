package org.openscada.da.server.sysinfo;


import java.io.File;

import org.openscada.da.core.common.DataItemCommand;
import org.openscada.da.core.common.MemoryDataItem;
import org.openscada.da.core.common.impl.HiveCommon;
import org.openscada.da.core.data.Variant;
import org.openscada.da.server.sysinfo.items.LoadAverageJob;
import org.openscada.da.server.sysinfo.items.MemoryCellItem;
import org.openscada.da.server.sysinfo.items.PlainFileDataItem;
import org.openscada.da.server.sysinfo.items.TimeDataItem;
import org.openscada.da.server.sysinfo.items.WriteDelayItem;
import org.openscada.utils.timing.Scheduler;

public class Hive extends HiveCommon {
	
	private Scheduler _scheduler = new Scheduler();
	
	public Hive ()
	{
		super();
		
		registerItem(new MemoryDataItem("memory") );
		
		DataItemCommand cmd;
		cmd = new DataItemCommand("hello");
		cmd.addListener(new DataItemCommand.Listener(){

			public void command(Variant value) {
				System.out.println ( "Hello World!" );
			}});
		registerItem ( cmd );
        
        cmd = new DataItemCommand("command");
        cmd.addListener(new DataItemCommand.Listener(){

            public void command(Variant value) {
                System.out.println ( "Command is: " + value.asString("<null>") );
            }});
        registerItem ( cmd );
        
		registerItem ( new TimeDataItem("time", _scheduler) );
		registerItem ( new PlainFileDataItem("hostname",new File("/proc/sys/kernel/hostname"), _scheduler, 1000 * 10 ) );
		registerItem ( new PlainFileDataItem("file",new File("/tmp/da.txt"), _scheduler, 1000 ) );
        registerItem ( new WriteDelayItem ( "write-delay" ) );
        registerItem ( new MemoryCellItem ( this, "memory-cell" ) );
		
		_scheduler.addJob ( new LoadAverageJob(this), 1000 );
	}
}
