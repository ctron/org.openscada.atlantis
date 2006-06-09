package org.openscada.da.server.snmp;


import java.util.HashMap;

import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.common.DataItemCommand;
import org.openscada.da.core.common.MemoryDataItem;
import org.openscada.da.core.common.impl.FolderCommon;
import org.openscada.da.core.common.impl.HiveCommon;
import org.openscada.da.core.data.Variant;
import org.openscada.da.server.snmp.items.MemoryCellItem;
import org.openscada.da.server.snmp.items.TimeDataItem;
import org.openscada.da.server.snmp.items.WriteDelayItem;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.lang.Pair;
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
        rootFolder.add ( "test", testFolder, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "This folder contains numerous test data items!" ) )
                .getMap ()
        );
        FolderCommon helloWorldFolder = new FolderCommon ();
        rootFolder.add ( "Hello World!", helloWorldFolder, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "This folder hello world items! Actually there are several tree entries that point to one item instance!" ) )
                .getMap ()
        );
        
        
        DataItem item;
        
		registerItem ( item = new MemoryDataItem ( "memory" ) );
		testFolder.add ( "memory", item, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "A memory cell that simply maps the output to its input." ) )
		        .getMap ()
		);
        
		DataItemCommand cmd;
		cmd = new DataItemCommand ( "hello" );
		cmd.addListener ( new DataItemCommand.Listener(){

			public void command(Variant value) {
				System.out.println ( "Hello World!" );
			}});
		registerItem ( item = cmd );
        helloWorldFolder.add ( "hello world", item, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "This cell triggers a command on the server. On the server it will print out 'Hello World'. On the client side you will see nothing ;-)" ) )
                .put ( "lang", new Variant ( "en" ) )
                .getMap ()
        );
        helloWorldFolder.add ( "おはよう", item, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "This cell triggers a command on the server. On the server it will print out 'Hello World'. On the client side you will see nothing ;-)" ) )
                .put ( "lang", new Variant ( "ja" ) )
                .getMap ()
        );
        helloWorldFolder.add ( "你好", item, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "This cell triggers a command on the server. On the server it will print out 'Hello World'. On the client side you will see nothing ;-)" ) )
                .put ( "lang", new Variant ( "zh" ) )
                .getMap ()
        );
        helloWorldFolder.add ( "नमस्ते", item, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "This cell triggers a command on the server. On the server it will print out 'Hello World'. On the client side you will see nothing ;-)" ) )
                .put ( "lang", new Variant ( "hi" ) )
                .getMap ()
        );
        helloWorldFolder.add ( "Hallo Welt!", item, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "This cell triggers a command on the server. On the server it will print out 'Hello World'. On the client side you will see nothing ;-)" ) )
                .put ( "lang", new Variant ( "de" ) )
                .getMap ()
        );
        
        cmd = new DataItemCommand ( "command" );
        cmd.addListener ( new DataItemCommand.Listener() {

            public void command(Variant value) {
                System.out.println ( "Command is: " + value.asString ( "<null>" ) );
            }});
        registerItem ( item = cmd );
        testFolder.add ( "command", item, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "Like the 'hello world' item it will print out something on the server. Instead of using a fixed string the value that was written to it is used." ) )
                .getMap ()
        );
        
		registerItem ( item = new TimeDataItem ( "time", _scheduler ) );
        testFolder.add ( "time", item, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "Need the unix time in microseconds? You get it here!" ) )
                .getMap ()
        );
        testFolder.add ( String.valueOf ( System.currentTimeMillis () ), item, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "Alias to 'time' but with a name that will change every server startup." ) )
                .getMap ()
        );
        
        registerItem ( item = new WriteDelayItem ( "write-delay" ) );
        testFolder.add ( "write delay", item, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "Simulate a long running write operation here. The value written to the data item is used as microsecond delay that the write operation will take." ) )
                .getMap ()
        );
        
        FolderCommon memoryFolder = new FolderCommon ();
        rootFolder.add ( "memory-cell", memoryFolder, new HashMap<String, Variant> () );
        registerItem ( item = new MemoryCellItem ( this, "memory-cell", memoryFolder ) );
        memoryFolder.add ( "control", item, new MapBuilder<String, Variant>()
                .put ( "description", new Variant ( "This is the control item of the data cell. Write to number of cells you want to this item. The memory cells wil be created dynamically." ) )
                .getMap ()
                );
        
	}
}
