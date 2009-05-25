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

package org.openscada.da.server.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlException;
import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.browser.common.query.GroupFolder;
import org.openscada.da.server.browser.common.query.GroupProvider;
import org.openscada.da.server.browser.common.query.IDNameProvider;
import org.openscada.da.server.browser.common.query.ItemDescriptor;
import org.openscada.da.server.browser.common.query.Matcher;
import org.openscada.da.server.browser.common.query.NullNameProvider;
import org.openscada.da.server.browser.common.query.QueryFolder;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.MemoryDataItem;
import org.openscada.da.server.common.chain.storage.ChainStorageServiceHelper;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.da.server.common.configuration.Configurator;
import org.openscada.da.server.common.configuration.xml.XMLConfigurator;
import org.openscada.da.server.common.factory.DataItemValidator;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.test.items.FactoryMemoryCell;
import org.openscada.da.server.test.items.MemoryCellItem;
import org.openscada.da.server.test.items.MemoryChainedItem;
import org.openscada.da.server.test.items.SuspendItem;
import org.openscada.da.server.test.items.TestItem1;
import org.openscada.da.server.test.items.TestItem2;
import org.openscada.da.server.test.items.TimeDataItem;
import org.openscada.da.server.test.items.WriteDelayItem;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.timing.Scheduler;

public class Hive extends HiveCommon
{

    private final Scheduler _scheduler = new Scheduler ( "TestHiveScheduler" );

    private final List<ItemDescriptor> _changingItems = new LinkedList<ItemDescriptor> ();

    private QueryFolder _queryFolderFactory = null;

    private final List<DataItem> _transientItems = new LinkedList<DataItem> ();

    private FolderCommon _testFolder = null;

    @SuppressWarnings ( "unused" )
    private FolderItemFactory itemFactory;

    // private ObjectExporter objectExporter;

    public Hive () throws ConfigurationError, IOException, XmlException
    {
        this ( null );
    }

    public Hive ( final Configurator configurator ) throws ConfigurationError, IOException, XmlException
    {
        super ();

        ChainStorageServiceHelper.registerDefaultPropertyService ( this );

        // create root folder
        final FolderCommon rootFolder = new FolderCommon ();
        setRootFolder ( rootFolder );

        // create and register test folder
        this._testFolder = new FolderCommon ();
        rootFolder.add ( "test", this._testFolder, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "This folder contains numerous test data items!" ) ).getMap () );
        final FolderCommon helloWorldFolder = new FolderCommon ();
        rootFolder.add ( "Hello World!", helloWorldFolder, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "This folder hello world items! Actually there are several tree entries that point to one item instance!" ) ).getMap () );

        // query folders
        final QueryFolder queryFolderRoot = new QueryFolder ( new Matcher () {

            public boolean matches ( final ItemDescriptor desc )
            {
                return true;
            }
        }, new NullNameProvider () );
        final QueryFolder queryFolder1 = new QueryFolder ( new Matcher () {

            public boolean matches ( final ItemDescriptor desc )
            {
                return desc.getItem ().getInformation ().getName ().matches ( ".*e+.*" );
            }
        }, new IDNameProvider () );

        queryFolderRoot.addChild ( "query", queryFolder1, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "contains items the have an 'e' in their id" ) ).getMap () );
        this._testFolder.add ( "storage", queryFolderRoot, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "storage based folder for grouping and query folders" ) ).getMap () );

        // memory cell factory
        this._queryFolderFactory = new QueryFolder ( new Matcher () {

            public boolean matches ( final ItemDescriptor desc )
            {
                return desc.getItem ().getInformation ().getName ().matches ( "memory\\.[a-z0-9]+" );
            }
        }, new IDNameProvider () );
        this._testFolder.add ( "memory-factory", this._queryFolderFactory, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "storage folder for items automatically created by the memory cell factory" ) ).getMap () );

        // Group Folders
        final GroupFolder groupFolder = new GroupFolder ( new GroupProvider () {

            public String[] getGrouping ( final ItemDescriptor descriptor )
            {
                final String id = descriptor.getItem ().getInformation ().getName ();
                if ( id.length () >= 2 )
                {
                    return new String[] { String.valueOf ( id.charAt ( 0 ) ), String.valueOf ( id.charAt ( 1 ) ) };
                }
                else
                {
                    return null;
                }
            }
        }, new IDNameProvider () );
        queryFolderRoot.addChild ( "grouping1", groupFolder, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Items with an ID of lenght >=2 will be pre-grouped by their first two characters" ) ).getMap () );

        DataItem item;
        final MapBuilder<String, Variant> builder = new MapBuilder<String, Variant> ();

        registerItem ( item = new MemoryDataItem ( "memory" ) );
        this._testFolder.add ( "memory", item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "A memory cell that simply maps the output to its input." ) ).getMap () );

        registerItem ( item = new TestItem2 ( this, "memory-chained" ) );
        this._testFolder.add ( "memory-chained", item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "A memory cell that simply maps the output to its input using a chain." ) ).getMap () );

        registerItem ( item = new TestItem1 ( "test-1" ) );
        this._testFolder.add ( "test-1", item, new MapBuilder<String, Variant> ().getMap () );

        DataItemCommand cmd;
        cmd = new DataItemCommand ( "hello" );
        cmd.addListener ( new DataItemCommand.Listener () {

            public void command ( final Variant value )
            {
                System.out.println ( "Hello World!" );
            }
        } );
        registerItem ( item = cmd );
        helloWorldFolder.add ( "hello world", item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "This cell triggers a command on the server. On the server it will print out 'Hello World'. On the client side you will see nothing ;-)" ) ).put ( "lang", new Variant ( "en" ) ).getMap () );
        helloWorldFolder.add ( "おはよう", item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "This cell triggers a command on the server. On the server it will print out 'Hello World'. On the client side you will see nothing ;-)" ) ).put ( "lang", new Variant ( "ja" ) ).getMap () );
        helloWorldFolder.add ( "你好", item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "This cell triggers a command on the server. On the server it will print out 'Hello World'. On the client side you will see nothing ;-)" ) ).put ( "lang", new Variant ( "zh" ) ).getMap () );
        helloWorldFolder.add ( "नमस्ते", item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "This cell triggers a command on the server. On the server it will print out 'Hello World'. On the client side you will see nothing ;-)" ) ).put ( "lang", new Variant ( "hi" ) ).getMap () );
        helloWorldFolder.add ( "Hallo Welt!", item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "This cell triggers a command on the server. On the server it will print out 'Hello World'. On the client side you will see nothing ;-)" ) ).put ( "lang", new Variant ( "de" ) ).getMap () );

        cmd = new DataItemCommand ( "command" );
        cmd.addListener ( new DataItemCommand.Listener () {

            public void command ( final Variant value )
            {
                System.out.println ( "Command is: " + value.asString ( "<null>" ) );
            }
        } );
        registerItem ( item = cmd );
        this._testFolder.add ( "command", item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Like the 'hello world' item it will print out something on the server. Instead of using a fixed string the value that was written to it is used." ) ).getMap () );

        registerItem ( item = new TimeDataItem ( "time", this._scheduler ) );
        builder.clear ();
        builder.put ( "description", new Variant ( "Need the unix time in microseconds? You get it here!" ) );
        this._testFolder.add ( "time", item, builder.getMap () );
        this._changingItems.add ( new ItemDescriptor ( item, builder.getMap () ) );

        this._testFolder.add ( String.valueOf ( System.currentTimeMillis () ), item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Alias to 'time' but with a name that will change every server startup." ) ).getMap () );

        final MemoryChainedItem memoryChainedItem = new MemoryChainedItem ( this, "chained" );
        registerItem ( memoryChainedItem );
        this._testFolder.add ( "chained", memoryChainedItem, new MapBuilder<String, Variant> ().getMap () );

        registerItem ( item = new WriteDelayItem ( "write-delay" ) );
        this._testFolder.add ( "write delay", item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Simulate a long running write operation here. The value written to the data item is used as microsecond delay that the write operation will take." ) ).getMap () );

        registerItem ( item = new SuspendItem ( "suspendable" ) );
        builder.clear ();
        builder.put ( "description", new Variant ( "This item is suspendable and will print is suspend status when it changes. WriteAttributeResult can only be seen on the server itself." ) );
        this._testFolder.add ( "suspendable", item, builder.getMap () );
        this._changingItems.add ( new ItemDescriptor ( item, builder.getMap () ) );

        final FolderCommon memoryFolder = new FolderCommon ();
        rootFolder.add ( "memory-cell", memoryFolder, new HashMap<String, Variant> () );
        registerItem ( item = new MemoryCellItem ( this, "memory-cell", memoryFolder ) );
        memoryFolder.add ( "control", item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "This is the control item of the data cell. Write to number of cells you want to this item. The memory cells wil be created dynamically." ) ).getMap () );
        addDataItemValidator ( new DataItemValidator () {

            public boolean isValid ( final String itemId )
            {
                return itemId.matches ( "memory-cell-[0-9]+" );
            }
        } );

        // do some stuff in the query folders
        final Thread changeThread = new Thread ( new Runnable () {

            public void run ()
            {
                while ( true )
                {
                    for ( final ItemDescriptor desc : Hive.this._changingItems )
                    {
                        queryFolderRoot.added ( desc );
                    }
                    addTransientItems ();
                    try
                    {
                        Thread.sleep ( 5 * 1000 );
                    }
                    catch ( final InterruptedException e )
                    {
                        e.printStackTrace ();
                    }
                    for ( final ItemDescriptor desc : Hive.this._changingItems )
                    {
                        queryFolderRoot.removed ( desc );
                    }
                    removeTransientItems ();
                    try
                    {
                        Thread.sleep ( 5 * 1000 );
                    }
                    catch ( final InterruptedException e )
                    {
                        e.printStackTrace ();
                    }
                }
            }
        } );
        changeThread.setDaemon ( true );
        changeThread.start ();

        /*
        this.itemFactory = new FolderItemFactory ( this, rootFolder, "itemFactory", "itemFactory");
        this.objectExporter = new ObjectExporter ( itemFactory );
        this.objectExporter.setInput ( new TestModelObject () );
        */

        if ( configurator == null )
        {
            xmlConfigure ();
        }
        else
        {
            configurator.configure ( this );
        }
    }

    private void xmlConfigure () throws ConfigurationError, IOException, XmlException
    {
        final String configurationFile = System.getProperty ( "openscada.da.hive.configuration" );
        if ( configurationFile != null )
        {
            final File file = new File ( configurationFile );
            xmlConfigure ( file );
        }
    }

    private void xmlConfigure ( final File file ) throws ConfigurationError, XmlException, IOException
    {
        new XMLConfigurator ( file ).configure ( this );
    }

    public void addMemoryFactoryItem ( final FactoryMemoryCell item, final Map<String, Variant> browserAttributes )
    {
        final ItemDescriptor desc = new ItemDescriptor ( item, browserAttributes );
        this._queryFolderFactory.added ( desc );
    }

    public void removeMemoryFactoryItem ( final FactoryMemoryCell item )
    {
        unregisterItem ( item );
        this._queryFolderFactory.removeAllForItem ( item );
    }

    protected void addTransientItems ()
    {
        DataItem transientItem;

        transientItem = new FactoryMemoryCell ( this, "transient-memory-cell-1" );
        registerItem ( transientItem );
        this._testFolder.add ( "transient", transientItem, new HashMap<String, Variant> () );
        this._transientItems.add ( transientItem );

        transientItem = new TimeDataItem ( "transient-time", this._scheduler );
        registerItem ( transientItem );
        this._testFolder.add ( "transient-time", transientItem, new HashMap<String, Variant> () );
        this._transientItems.add ( transientItem );
    }

    protected void removeTransientItems ()
    {
        for ( DataItem transientItem : this._transientItems )
        {
            this._testFolder.remove ( transientItem );
            unregisterItem ( transientItem );
            transientItem = null;
        }
        this._transientItems.clear ();
    }
}
