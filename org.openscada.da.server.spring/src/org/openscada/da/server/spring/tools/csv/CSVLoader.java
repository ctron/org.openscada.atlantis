/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.spring.tools.csv;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.browser.common.query.ItemStorage;
import org.openscada.da.server.spring.Loader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.ConcurrentExecutorAdapter;
import org.springframework.util.Assert;

import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;

public class CSVLoader extends Loader implements InitializingBean
{
    private static Logger logger = Logger.getLogger ( CSVLoader.class );

    private Resource resource;

    private String data;

    private String[] mapping = new String[] { "id", "readable", "writable", "description", "initialValue" };

    private int skipLines = 0;

    private Collection<ItemStorage> _controllerStorages = new LinkedList<ItemStorage> ();

    private Executor executor = new ConcurrentExecutorAdapter ( new SimpleAsyncTaskExecutor () );

    public void setExecutor ( final TaskExecutor executor )
    {
        this.executor = new ConcurrentExecutorAdapter ( executor );
    }

    public void setExecutor ( final Executor executor )
    {
        this.executor = executor;
    }

    public void setResource ( final Resource resource )
    {
        this.resource = resource;
    }

    public void setData ( final String data )
    {
        this.data = data;
    }

    public void setSkipLines ( final int skipLines )
    {
        this.skipLines = skipLines;
    }

    public void setControllerStorages ( final Collection<ItemStorage> controllerStorages )
    {
        this._controllerStorages = controllerStorages;
    }

    @Override
    public void afterPropertiesSet () throws Exception
    {
        Assert.notNull ( this.hive, "'hive' must not be null" );
        Assert.state ( this.resource != null || this.data != null, "'resource' and 'data' are both unset. One must be set!" );
        Assert.notNull ( this.storages, "'storages' must not be null" );

        load ();
    }

    protected void load ( final Reader reader, final String sourceName )
    {
        final ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy ();
        strat.setColumnMapping ( this.mapping );
        strat.setType ( ItemEntry.class );

        final CsvToBean<ItemEntry> bean = new CsvToBean<ItemEntry> ();
        final Collection<ItemEntry> beans = bean.parse ( this.skipLines, strat, reader );
        for ( final ItemEntry entry : beans )
        {
            entry.setId ( entry.getId ().trim () );
            if ( entry.getId ().length () > 0 )
            {
                createItem ( entry, sourceName );
            }
        }
    }

    private void load () throws IOException
    {
        if ( this.resource != null )
        {
            final Reader reader = new InputStreamReader ( this.resource.getInputStream () );
            load ( reader, this.resource.toString () );
            reader.close ();
        }
        if ( this.data != null )
        {
            final Reader reader = new StringReader ( this.data );
            load ( reader, "inline data" );
            reader.close ();
        }
    }

    private void createItem ( final ItemEntry entry, final String sourceName )
    {
        final EnumSet<IODirection> io = EnumSet.noneOf ( IODirection.class );
        if ( entry.isReadable () )
        {
            io.add ( IODirection.INPUT );
        }
        if ( entry.isWritable () )
        {
            io.add ( IODirection.OUTPUT );
        }

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "description", new Variant ( entry.getDescription () ) );
        attributes.put ( "loader.csv.source", new Variant ( sourceName ) );
        attributes.put ( "initialValue", new Variant ( entry.getInitialValue () ) );

        final CSVDataItem item = new CSVDataItem ( this.hive, this.itemPrefix + entry.getId (), io );
        injectItem ( item, attributes );

        // create and inject the controller item
        attributes.put ( "loader.csv.controllerFor", new Variant ( this.itemPrefix + entry.getId () ) );
        final CSVControllerDataItem controllerItem = new CSVControllerDataItem ( item, this.executor );
        Loader.injectItem ( this.hive, this._controllerStorages, controllerItem, attributes );

        // set the initial value
        try
        {
            controllerItem.startWriteValue ( entry.getInitialValue (), null ).get ();
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to set initial value: " + entry.getInitialValue (), e );
        }
    }

    public void setMapping ( final String[] mapping )
    {
        this.mapping = mapping;
    }
}
