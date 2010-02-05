/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2007 inavare GmbH (http://inavare.com)
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

    private Resource _resource;

    private String _data;

    private String[] _mapping = new String[] { "id", "readable", "writable", "description", "initialValue" };

    private int _skipLines = 0;

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
        this._resource = resource;
    }

    public void setData ( final String data )
    {
        this._data = data;
    }

    public void setSkipLines ( final int skipLines )
    {
        this._skipLines = skipLines;
    }

    public void setControllerStorages ( final Collection<ItemStorage> controllerStorages )
    {
        this._controllerStorages = controllerStorages;
    }

    public void afterPropertiesSet () throws Exception
    {
        Assert.notNull ( this._hive, "'hive' must not be null" );
        Assert.state ( this._resource != null || this._data != null, "'resource' and 'data' are both unset. One must be set!" );
        Assert.notNull ( this._storages, "'storages' must not be null" );

        load ();
    }

    protected void load ( final Reader reader, final String sourceName )
    {
        final ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy ();
        strat.setColumnMapping ( this._mapping );
        strat.setType ( ItemEntry.class );

        final CsvToBean<ItemEntry> bean = new CsvToBean<ItemEntry> ();
        final Collection<ItemEntry> beans = bean.parse ( this._skipLines, strat, reader );
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
        if ( this._resource != null )
        {
            final Reader reader = new InputStreamReader ( this._resource.getInputStream () );
            load ( reader, this._resource.toString () );
            reader.close ();
        }
        if ( this._data != null )
        {
            final Reader reader = new StringReader ( this._data );
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

        final CSVDataItem item = new CSVDataItem ( this._hive, this._itemPrefix + entry.getId (), io );
        injectItem ( item, attributes );

        // create and inject the controller item
        attributes.put ( "loader.csv.controllerFor", new Variant ( this._itemPrefix + entry.getId () ) );
        final CSVControllerDataItem controllerItem = new CSVControllerDataItem ( item, this.executor );
        Loader.injectItem ( this._hive, this._controllerStorages, controllerItem, attributes );

        // set the initial value
        try
        {
            controllerItem.startWriteValue ( null, entry.getInitialValue () ).get ();
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to set initial value: " + entry.getInitialValue (), e );
        }
    }

    public void setMapping ( final String[] mapping )
    {
        this._mapping = mapping;
    }
}
