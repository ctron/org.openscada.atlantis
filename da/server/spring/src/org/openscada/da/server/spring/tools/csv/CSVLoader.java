package org.openscada.da.server.spring.tools.csv;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.spring.Loader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

public class CSVLoader extends Loader implements InitializingBean
{
    private Resource _resource;
    private String _data;
    private String [] _mapping = new String[] { "id", "readable", "writeable", "description" };

    public void setResource ( Resource resource )
    {
        this._resource = resource;
    }

    public void setData ( String data )
    {
        _data = data;
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

        CsvToBean bean = new CsvToBean ();
        List<?> beans = bean.parse ( strat, reader );
        for ( Object row : beans )
        {
            ItemEntry entry = (ItemEntry)row;
            entry.setId ( entry.getId ().trim () );
            if ( entry.getId ().length () > 0 )
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
        injectItem ( new CSVControllerDataItem ( item ), attributes );
    }

    public void setMapping ( String[] mapping )
    {
        _mapping = mapping;
    }
}
