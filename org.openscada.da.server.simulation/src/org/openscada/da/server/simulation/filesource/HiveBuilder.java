/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.simulation.filesource;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptException;

import org.openscada.core.Variant;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.sec.UserInformation;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class HiveBuilder
{
    /**
     * @param name
     * @param port
     * @return
     */
    public static HiveBuilder create ( final String name )
    {
        final HiveBuilder hiveBuilder = new HiveBuilder ();
        hiveBuilder.setName ( name );
        return hiveBuilder;
    }

    private final List<ItemDefinition> items = new ArrayList<ItemDefinition> ();

    private String name = "TEST";

    /**
     * always call {@link #create(String, Integer)} 
     */
    private HiveBuilder ()
    {
    }

    /**
     * @param name
     * @return
     */
    public ItemDefinition addInputItem ( final String name )
    {
        final ItemDefinition itemDefinition = addItem ();
        itemDefinition.setName ( name );
        itemDefinition.setDirection ( Direction.IN );
        return itemDefinition;
    }

    /**
     * @param name
     * @param callback
     * @return
     */
    public ItemDefinition addInputItem ( final String name, final String callback )
    {
        final ItemDefinition itemDefinition = addItem ();
        itemDefinition.setName ( name );
        itemDefinition.setDirection ( Direction.IN );
        itemDefinition.setCallback ( callback );
        return itemDefinition;
    }

    /**
     * @param name
     * @return
     */
    public ItemDefinition addInputOutputItem ( final String name )
    {
        final ItemDefinition itemDefinition = addItem ();
        itemDefinition.setName ( name );
        itemDefinition.setDirection ( Direction.INOUT );
        return itemDefinition;
    }

    /**
     * @param name
     * @param callback
     * @return
     */
    public ItemDefinition addInputOutputItem ( final String name, final String callback )
    {
        final ItemDefinition itemDefinition = addItem ();
        itemDefinition.setName ( name );
        itemDefinition.setDirection ( Direction.INOUT );
        itemDefinition.setCallback ( callback );
        return itemDefinition;
    }

    /**
     * @return
     */
    public ItemDefinition addItem ()
    {
        final ItemDefinition itemDefinition = new ItemDefinition ();
        this.items.add ( itemDefinition );
        return itemDefinition;
    }

    /**
     * @param itemDefinition
     * @return
     */
    public ItemDefinition addItem ( final ItemDefinition itemDefinition )
    {
        this.items.add ( itemDefinition );
        return itemDefinition;
    }

    /**
     * @param name
     * @return
     */
    public ItemDefinition addOutputItem ( final String name )
    {
        final ItemDefinition itemDefinition = addItem ();
        itemDefinition.setName ( name );
        itemDefinition.setDirection ( Direction.OUT );
        return itemDefinition;
    }

    /**
     * @param name
     * @param callback
     * @return
     */
    public ItemDefinition addOutputItem ( final String name, final String callback )
    {
        final ItemDefinition itemDefinition = addItem ();
        itemDefinition.setName ( name );
        itemDefinition.setDirection ( Direction.OUT );
        itemDefinition.setCallback ( callback );
        return itemDefinition;
    }

    public ItemDefinition addOutputItem ( final String name, final String callback, final String writeHandler )
    {
        final ItemDefinition itemDefinition = addItem ();
        itemDefinition.setName ( name );
        itemDefinition.setDirection ( Direction.OUT );
        itemDefinition.setCallback ( callback );
        itemDefinition.setWriteHandler ( writeHandler );
        return itemDefinition;
    }

    public String getName ()
    {
        return this.name;
    }

    /**
     * 
     */
    public void makeExample ()
    {
        addInputItem ( "sample.item.item1", "function(item) { onInterval(item, 1111, function(item) { doIncrementInt32(1, item); }) }" );
        addInputItem ( "sample.item.item2", "function(item) { onInterval(item, 3333, function(item) { doIncrementInt64(3, item); }) }" );
        final ItemDefinition item = addInputItem ( "sample.item.item3", "function(item) { onInterval(item, 6666, function(item) { doIncrementDouble(0.7, item); }) }" );
        item.addAttr ( "Error", false );
        item.addAttr ( "Alarm", false );
    }

    /**
     * @param hive
     * @throws Exception 
     */
    public void registerItem ( final Hive hive, final ItemDefinition itemDefinition ) throws Exception
    {
        final FolderItemFactory factory = hive.getFactory ();
        try
        {
            hive.getScriptEngine ().put ( "server", hive );
            if ( Direction.IN.equals ( itemDefinition.getDirection () ) )
            {
                final DataItemInputChained item = factory.createInput ( itemDefinition.getName () );
                item.updateData ( new Variant ( itemDefinition.getDefaultValue () ), itemDefinition.getAttributes (), AttributeMode.SET );
                hive.getScriptEngine ().put ( "item", item );
                hive.getScriptEngine ().eval ( "registerItem(server, item, " + itemDefinition.getCallback () + ");" );
            }
            else if ( Direction.OUT.equals ( itemDefinition.getDirection () ) )
            {
                final DataItemCommand item = factory.createCommand ( itemDefinition.getName () );
                item.startSetAttributes ( null, item.getAttributes () );
                hive.getScriptEngine ().put ( "item", item );
                hive.getScriptEngine ().eval ( "registerItem(server, item, " + itemDefinition.getCallback () + ");" );
            }
            else if ( Direction.INOUT.equals ( itemDefinition.getDirection () ) )
            {
                final WriteHandlerItem item = factory.createInputOutput ( itemDefinition.getName (), null );
                item.updateData ( new Variant ( itemDefinition.getDefaultValue () ), itemDefinition.getAttributes (), AttributeMode.SET );
                hive.getScriptEngine ().put ( "item", item );
                final int i = ( (Double)hive.getScriptEngine ().eval ( "registerItem(server, item, " + itemDefinition.getCallback () + ", " + itemDefinition.getWriteHandler () + ");" ) ).intValue ();
                item.setWriteHandler ( new WriteHandler () {
                    public void handleWrite ( final UserInformation userInformation, final Variant value ) throws Exception
                    {
                        hive.getScriptEngine ().put ( "value", value );
                        hive.getScriptEngine ().eval ( "writeValue(" + i + ", value);" );
                    }
                } );
            }
        }
        catch ( final ScriptException e )
        {
            throw new RuntimeException ( e );
        }
    }

    public void setName ( final String name )
    {
        this.name = name;
    }

    /**
     * @return
     * @throws Exception
     */
    public Hive configureHive ( final Hive hive ) throws Exception
    {
        for ( final ItemDefinition itemDefinition : this.items )
        {
            registerItem ( hive, itemDefinition );
        }
        return hive;
    }
}
