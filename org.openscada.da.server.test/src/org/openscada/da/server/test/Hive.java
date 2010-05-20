/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

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
import org.openscada.da.server.common.exporter.ObjectExporter;
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
import org.openscada.da.server.test.model.TestModelObject;
import org.openscada.utils.collection.MapBuilder;

public class Hive extends HiveCommon
{

    private final List<ItemDescriptor> changingItems = new LinkedList<ItemDescriptor> ();

    private QueryFolder queryFolderFactory = null;

    private final List<DataItem> transientItems = new LinkedList<DataItem> ();

    private FolderCommon testFolder = null;

    @SuppressWarnings ( "unused" )
    private FolderItemFactory itemFactory;

    private final Timer timer;

    private ObjectExporter objectExporter;

    private TestModelObject testObject;

    public Hive () throws ConfigurationError, IOException, XmlException
    {
        this ( null );
    }

    public Hive ( final Configurator configurator ) throws ConfigurationError, IOException, XmlException
    {
        super ();

        ChainStorageServiceHelper.registerDefaultPropertyService ( this );

        this.timer = new Timer ( true );

        // create root folder
        final FolderCommon rootFolder = new FolderCommon ();
        setRootFolder ( rootFolder );

        // create and register test folder
        this.testFolder = new FolderCommon ();
        rootFolder.add ( "test", this.testFolder, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "This folder contains numerous test data items!" ) ).getMap () );
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
        this.testFolder.add ( "storage", queryFolderRoot, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "storage based folder for grouping and query folders" ) ).getMap () );

        // memory cell factory
        this.queryFolderFactory = new QueryFolder ( new Matcher () {

            public boolean matches ( final ItemDescriptor desc )
            {
                return desc.getItem ().getInformation ().getName ().matches ( "memory\\.[a-z0-9]+" );
            }
        }, new IDNameProvider () );
        this.testFolder.add ( "memory-factory", this.queryFolderFactory, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "storage folder for items automatically created by the memory cell factory" ) ).getMap () );

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
        this.testFolder.add ( "memory", item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "A memory cell that simply maps the output to its input." ) ).getMap () );

        registerItem ( item = new TestItem2 ( this, "memory-chained" ) );
        this.testFolder.add ( "memory-chained", item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "A memory cell that simply maps the output to its input using a chain." ) ).getMap () );

        registerItem ( item = new TestItem1 ( "test-1" ) );
        this.testFolder.add ( "test-1", item, new MapBuilder<String, Variant> ().getMap () );

        DataItemCommand cmd;
        cmd = new DataItemCommand ( "hello", this.getOperationService () );
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

        cmd = new DataItemCommand ( "command", this.getOperationService () );
        cmd.addListener ( new DataItemCommand.Listener () {

            public void command ( final Variant value )
            {
                System.out.println ( "Command is: " + value.asString ( "<null>" ) );
            }
        } );
        registerItem ( item = cmd );
        this.testFolder.add ( "command", item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Like the 'hello world' item it will print out something on the server. Instead of using a fixed string the value that was written to it is used." ) ).getMap () );

        registerItem ( item = new TimeDataItem ( "time", this.timer ) );
        builder.clear ();
        builder.put ( "description", new Variant ( "Need the unix time in microseconds? You get it here!" ) );
        this.testFolder.add ( "time", item, builder.getMap () );
        this.changingItems.add ( new ItemDescriptor ( item, builder.getMap () ) );

        this.testFolder.add ( String.valueOf ( System.currentTimeMillis () ), item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Alias to 'time' but with a name that will change every server startup." ) ).getMap () );

        final MemoryChainedItem memoryChainedItem = new MemoryChainedItem ( this, "chained" );
        registerItem ( memoryChainedItem );
        this.testFolder.add ( "chained", memoryChainedItem, new MapBuilder<String, Variant> ().getMap () );

        registerItem ( item = new WriteDelayItem ( "write-delay", getOperationService () ) );
        this.testFolder.add ( "write delay", item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Simulate a long running write operation here. The value written to the data item is used as microsecond delay that the write operation will take." ) ).getMap () );

        registerItem ( item = new SuspendItem ( "suspendable" ) );
        builder.clear ();
        builder.put ( "description", new Variant ( "This item is suspendable and will print is suspend status when it changes. WriteAttributeResult can only be seen on the server itself." ) );
        this.testFolder.add ( "suspendable", item, builder.getMap () );
        this.changingItems.add ( new ItemDescriptor ( item, builder.getMap () ) );

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
                    for ( final ItemDescriptor desc : Hive.this.changingItems )
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
                    for ( final ItemDescriptor desc : Hive.this.changingItems )
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

        setupExporter ( rootFolder );

        if ( configurator == null )
        {
            xmlConfigure ();
        }
        else
        {
            configurator.configure ( this );
        }
    }

    private void setupExporter ( final FolderCommon rootFolder )
    {
        this.objectExporter = new ObjectExporter ( "objectExporter", this, rootFolder );
        this.objectExporter.attachTarget ( this.testObject = new TestModelObject () );
        this.testObject.setLongValue ( 1234 );
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
        this.queryFolderFactory.added ( desc );
    }

    public void removeMemoryFactoryItem ( final FactoryMemoryCell item )
    {
        unregisterItem ( item );
        this.queryFolderFactory.removeAllForItem ( item );
    }

    protected void addTransientItems ()
    {
        DataItem transientItem;

        transientItem = new FactoryMemoryCell ( this, "transient-memory-cell-1" );
        registerItem ( transientItem );
        this.testFolder.add ( "transient", transientItem, new HashMap<String, Variant> () );
        this.transientItems.add ( transientItem );

        transientItem = new TimeDataItem ( "transient-time", this.timer );
        registerItem ( transientItem );
        this.testFolder.add ( "transient-time", transientItem, new HashMap<String, Variant> () );
        this.transientItems.add ( transientItem );
    }

    protected void removeTransientItems ()
    {
        for ( DataItem transientItem : this.transientItems )
        {
            this.testFolder.remove ( transientItem );
            unregisterItem ( transientItem );
            transientItem = null;
        }
        this.transientItems.clear ();
    }
}
