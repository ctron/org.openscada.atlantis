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

import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.browser.common.query.ItemStorage;
import org.openscada.da.server.spring.Loader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;

public class CSVLoader extends Loader implements InitializingBean
{
    private Resource _resource;
    private String _data;
    private String [] _mapping = new String[] { "id", "readable", "writable", "description" };
    private int _skipLines = 0;
    
    private Collection<ItemStorage> _controllerStorages = new LinkedList<ItemStorage> ();

    public void setResource ( Resource resource )
    {
        this._resource = resource;
    }

    public void setData ( String data )
    {
        _data = data;
    }
    
    public void setSkipLines ( int skipLines )
    {
        _skipLines = skipLines;
    }
    
    public void setControllerStorages ( Collection<ItemStorage> controllerStorages )
    {
        _controllerStorages = controllerStorages;
    }

    public void afterPropertiesSet () throws Exception
    {
        Assert.notNull ( _hive, "'hive' must not be null" );
        Assert.state ( _resource != null || _data != null, "'resource' and 'data' are both unset. One must be set!" );
        Assert.notNull ( _storages, "'storages' must not be null" );

        load ();
    }

    protected void load ( Reader reader, String sourceName )
    {
        ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy ();
        strat.setColumnMapping ( _mapping );
        strat.setType ( ItemEntry.class );
        
        CsvToBean<ItemEntry> bean = new CsvToBean<ItemEntry> ();
        Collection<ItemEntry> beans = bean.parse ( _skipLines, strat, reader );
        for ( ItemEntry entry : beans )
        {
            entry.setId ( entry.getId ().trim () );
            if ( entry.getId ().length () > 0)
            {
                createItem ( entry, sourceName );
            }
        }
    }

    private void load () throws IOException
    {
        if ( _resource != null )
        {
            Reader reader = new InputStreamReader ( this._resource.getInputStream () );
            load ( reader, _resource.toString () );
            reader.close ();
        }
        if ( _data != null  )
        {
            Reader reader = new StringReader ( _data );
            load ( reader, "inline data" );
            reader.close ();
        }
    }

    private void createItem ( ItemEntry entry, String sourceName )
    {
        EnumSet<IODirection> io = EnumSet.noneOf ( IODirection.class );
        if ( entry.isReadable () )
        {
            io.add ( IODirection.INPUT );
        }
        if ( entry.isWriteable () )
        {
            io.add ( IODirection.OUTPUT );
        }

        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "description", new Variant ( entry.getDescription () ) );
        attributes.put ( "loader.csv.source", new Variant ( sourceName ) );

        CSVDataItem item = new CSVDataItem ( _itemPrefix + entry.getId (), io );
        injectItem ( item, attributes );

        attributes.put ( "loader.csv.controllerFor", new Variant ( _itemPrefix + entry.getId () ) );
        Loader.injectItem ( _hive, _controllerStorages, new CSVControllerDataItem ( item ), attributes );
    }

    public void setMapping ( String[] mapping )
    {
        _mapping = mapping;
    }
}
