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

package org.openscada.da.server.test;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.openscada.da.core.IODirection;
import org.openscada.da.core.browser.common.FolderCommon;
import org.openscada.da.core.browser.common.query.GroupFolder;
import org.openscada.da.core.browser.common.query.GroupProvider;
import org.openscada.da.core.browser.common.query.IDNameProvider;
import org.openscada.da.core.browser.common.query.ItemDescriptor;
import org.openscada.da.core.browser.common.query.Matcher;
import org.openscada.da.core.browser.common.query.NullNameProvider;
import org.openscada.da.core.browser.common.query.QueryFolder;
import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.common.DataItemCommand;
import org.openscada.da.core.common.DataItemInformationBase;
import org.openscada.da.core.common.MemoryDataItem;
import org.openscada.da.core.common.chained.DataItemInputChained;
import org.openscada.da.core.common.chained.LevelAlarmChainItem;
import org.openscada.da.core.common.impl.HiveCommon;
import org.openscada.da.core.data.Variant;
import org.openscada.da.server.test.items.FactoryMemoryCell;
import org.openscada.da.server.test.items.MemoryCellFactory;
import org.openscada.da.server.test.items.MemoryCellItem;
import org.openscada.da.server.test.items.MemoryChainedItem;
import org.openscada.da.server.test.items.SuspendItem;
import org.openscada.da.server.test.items.TimeDataItem;
import org.openscada.da.server.test.items.WriteDelayItem;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.timing.Scheduler;

public class Hive extends HiveCommon {
	
	private Scheduler _scheduler = new Scheduler();
    
    private List<ItemDescriptor> _changingItems = new LinkedList<ItemDescriptor> ();
    
    private QueryFolder _queryFolderFactory = null;
	
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
        
        // query folders
        final QueryFolder queryFolderRoot = new QueryFolder ( new Matcher () {

            public boolean matches ( ItemDescriptor desc )
            {
                return true;
            }}, new NullNameProvider () );
        QueryFolder queryFolder1 = new QueryFolder ( new Matcher () {

            public boolean matches ( ItemDescriptor desc )
            {
                return desc.getItem ().getInformation ().getName ().matches ( ".*e+.*" );
            }}, new IDNameProvider () );
        
        queryFolderRoot.addChild ( "query", queryFolder1, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "contains items the have an 'e' in their id" ) )
                .getMap ()
        );
        testFolder.add ( "storage", queryFolderRoot, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "storage based folder for grouping and query folders" ) )
                .getMap ()
        );
        
        // memory cell factory
        _queryFolderFactory = new QueryFolder ( new Matcher () {

            public boolean matches ( ItemDescriptor desc )
            {
                return desc.getItem ().getInformation ().getName ().matches ( "memory\\.[a-z0-9]+" );
            }}, new IDNameProvider () );
        testFolder.add ( "memory-factory", _queryFolderFactory, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "storage folder for items automatically created by the memory cell factory" ) )
                .getMap ()
        );
        
        // Group Folders
        GroupFolder groupFolder = new GroupFolder ( new GroupProvider () {

            public String[] getGrouping ( ItemDescriptor descriptor )
            {
                String id = descriptor.getItem ().getInformation ().getName ();
                if ( id.length () >= 2 )
                    return new String [] {
                        String.valueOf ( id.charAt ( 0 ) ),
                        String.valueOf ( id.charAt ( 1 ) ) };
                else
                    return null;
            }}, new IDNameProvider () );
        queryFolderRoot.addChild ( "grouping1", groupFolder, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "Items with an ID of lenght >=2 will be pre-grouped by their first two characters" ) )
                .getMap ()
        );
        
        DataItem item;
        MapBuilder<String, Variant> builder = new MapBuilder<String, Variant> ();
        
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
        builder.clear ();
        builder.put ( "description", new Variant ( "Need the unix time in microseconds? You get it here!" ) );
        testFolder.add ( "time", item, builder.getMap () );
        _changingItems.add ( new ItemDescriptor ( item, builder.getMap () ) );
        
        testFolder.add ( String.valueOf ( System.currentTimeMillis () ), item, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "Alias to 'time' but with a name that will change every server startup." ) )
                .getMap ()
        );
       
        DataItemInputChained chainedItem = new MemoryChainedItem ( "chained-input" );
        registerItem ( chainedItem );
        testFolder.add ( "chained-input", chainedItem, new MapBuilder<String, Variant> ().getMap () );
        
        registerItem ( item = new WriteDelayItem ( "write-delay" ) );
        testFolder.add ( "write delay", item, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "Simulate a long running write operation here. The value written to the data item is used as microsecond delay that the write operation will take." ) )
                .getMap ()
        );
        
        registerItem ( item = new SuspendItem ( "suspendable" ) );
        builder.clear ();
        builder.put ( "description", new Variant ( "This item is suspendable and will print is suspend status when it changes. Result can only be seen on the server itself." ) );
        testFolder.add ( "suspendable", item, builder.getMap () );
        _changingItems.add ( new ItemDescriptor ( item, builder.getMap () ) );
        
        FolderCommon memoryFolder = new FolderCommon ();
        rootFolder.add ( "memory-cell", memoryFolder, new HashMap<String, Variant> () );
        registerItem ( item = new MemoryCellItem ( this, "memory-cell", memoryFolder ) );
        memoryFolder.add ( "control", item, new MapBuilder<String, Variant>()
                .put ( "description", new Variant ( "This is the control item of the data cell. Write to number of cells you want to this item. The memory cells wil be created dynamically." ) )
                .getMap ()
                );
        
        addItemFactory ( new MemoryCellFactory ( this ) );
        
        // do some stuff in the query folders
        Thread changeThread = new Thread ( new Runnable () {

            public void run ()
            {
                while ( true )
                {
                    for ( ItemDescriptor desc : _changingItems )
                    {
                        queryFolderRoot.added ( desc );
                    }
                    try
                    {
                        Thread.sleep ( 5 * 1000 );
                    }
                    catch ( InterruptedException e )
                    {
                        e.printStackTrace();
                    }
                    for ( ItemDescriptor desc : _changingItems )
                    {
                        queryFolderRoot.removed ( desc );
                    }
                    try
                    {
                        Thread.sleep ( 5 * 1000 );
                    }
                    catch ( InterruptedException e )
                    {
                        e.printStackTrace();
                    }
                }
            }} );
        changeThread.setDaemon ( true );
        changeThread.start ();
	}

    public void addMemoryFactoryItem ( FactoryMemoryCell item )
    {
        ItemDescriptor desc = new ItemDescriptor ( item, new MapBuilder<String, Variant> ().getMap () );
        _queryFolderFactory.added ( desc );
    }
    public void removeMemoryFactoryItem ( FactoryMemoryCell item )
    {
        unregisterItem ( item );
        _queryFolderFactory.removeAllForItem ( item );
    }
}
