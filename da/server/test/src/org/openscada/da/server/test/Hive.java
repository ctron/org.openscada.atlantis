package org.openscada.da.server.test;


import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.common.DataItemCommand;
import org.openscada.da.core.common.MemoryDataItem;
import org.openscada.da.core.common.impl.FolderCommon;
import org.openscada.da.core.common.impl.HiveCommon;
import org.openscada.da.core.data.Variant;
import org.openscada.da.server.test.items.MemoryCellItem;
import org.openscada.da.server.test.items.TimeDataItem;
import org.openscada.da.server.test.items.WriteDelayItem;
import org.openscada.utils.timing.Scheduler;

public class Hive extends HiveCommon {
	
	private Scheduler _scheduler = new Scheduler();
	
	public Hive ()
	{
		super();
		
        // create root folder
		FolderCommon rootFolder = new FolderCommon ();
        setRootFolder ( rootFolder );

        // create and register test folder
        FolderCommon testFolder = new FolderCommon ();
        rootFolder.add ( "test", testFolder );
        
        DataItem item;
		registerItem ( item = new MemoryDataItem("memory") );
		testFolder.add ( "memory", item );
        
		DataItemCommand cmd;
		cmd = new DataItemCommand ( "hello" );
		cmd.addListener ( new DataItemCommand.Listener(){

			public void command(Variant value) {
				System.out.println ( "Hello World!" );
			}});
		registerItem ( cmd );
        
        cmd = new DataItemCommand ( "command" );
        cmd.addListener ( new DataItemCommand.Listener() {

            public void command(Variant value) {
                System.out.println ( "Command is: " + value.asString ( "<null>" ) );
            }});
        registerItem ( cmd );
        
		registerItem ( new TimeDataItem ( "time", _scheduler ) );
        registerItem ( new WriteDelayItem ( "write-delay" ) );
        
        FolderCommon memoryFolder = new FolderCommon ();
        rootFolder.add ( "memory-cell", memoryFolder );
        registerItem ( item = new MemoryCellItem ( this, "memory-cell", memoryFolder ) );
        memoryFolder.add ( "control", item );
        
	}
}
