/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.net.da.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.ae.core.Variant;
import org.openscada.da.core.server.IODirection;
import org.openscada.da.core.server.browser.DataItemEntry;
import org.openscada.da.core.server.browser.Entry;
import org.openscada.da.core.server.browser.FolderEntry;
import org.openscada.net.base.data.IntegerValue;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.base.data.ValueTools;
import org.openscada.net.base.data.VoidValue;
import org.openscada.utils.lang.Holder;

public class ListBrowser
{
    private static Logger _log = Logger.getLogger ( ListBrowser.class );
    
    public static Message createRequest ( String [] path )
    {
        Message message = new Message ( Messages.CC_BROWSER_LIST_REQ );
     
        ListValue value = new ListValue ();
        for ( String tok : path )
        {
            value.add ( new StringValue ( tok ) );
        }
        message.getValues ().put ( "path", value );
        
        return message;
    }
    
    public static String [] parseRequest ( Message message )
    {
        List<String> list = new ArrayList<String> ();
        List<Value> listValue = null;
        
        if ( message.getValues ().containsKey ( "path" ) )
            if ( message.getValues ().get ( "path" ) instanceof ListValue )
                listValue = ((ListValue)message.getValues ().get ( "path" )).getValues ();

        if ( listValue == null )
            return new String[0];
        
        for ( Value value : listValue )
        {
            list.add ( value.toString () );
        }
        
        return list.toArray ( new String[0] );
    }
    
    private static void createEntries ( Message message, String field, Iterable<Entry> entries )
    {
        ListValue list = new ListValue ();
        for ( Entry entry : entries )
        {
            MapValue mapValue = new MapValue ();
            
            mapValue.put ( "name", new StringValue ( entry.getName () ) );
            mapValue.put ( "attributes", Messages.attributesToMap ( entry.getAttributes () ) );
            
            if ( entry instanceof FolderEntry )
            {
                mapValue.put ( "type", new StringValue ( "folder" ) );
            }
            else if ( entry instanceof DataItemEntry )
            {
                mapValue.put ( "type", new StringValue ( "item" ) );
                DataItemEntry dataItemEntry = (DataItemEntry)entry;
                mapValue.put ( "item-id", new StringValue ( dataItemEntry.getId () ) );
                mapValue.put ( "io-direction", new IntegerValue ( Messages.encodeIO ( dataItemEntry.getIODirections () )) );
            }
            else
            {
                mapValue.put ( "type", new StringValue ( "unknown" ) );
            }
            
            list.add ( mapValue );
        }
        message.getValues ().put ( field, list );
    }
    
    public static Message createResponse ( Message requestMessage, Entry [] entries )
    {
        Message message = new Message ( Messages.CC_BROWSER_LIST_RES, requestMessage.getSequence () );
        
        createEntries ( message, "entries", Arrays.asList ( entries ) );
        
        return message;
    }
    
    private static void parseEntries ( Message message, String field, List<Entry> list )
    {
        list.clear ();
        
        if ( !message.getValues ().containsKey ( field ) )
        {
            _log.warn ( "Required value '" + field + "' missing" );
            return;
        }
        
        if ( !(message.getValues ().get ( field ) instanceof ListValue) )
        {
            _log.warn ( "'" + field + "' must be of type 'list'" );
            return;
        }

        ListValue entries = (ListValue)message.getValues ().get ( field );
        
        for ( Value value : entries.getValues () )
        {
            if ( !(value instanceof MapValue) )
            {
                _log.warn ( "list value is not of type 'map'. Skipping!" );
                continue;
            }
            MapValue mapValue = (MapValue)value;
            
            Entry entry = null;
            if ( !mapValue.containsKey ( "type" ) )
            {
                _log.warn ( "map misses required value 'type'" );
                continue;
            }
            if ( !mapValue.containsKey ( "name" ) )
            {
                _log.warn ( "map misses required value 'name'" );
                continue;
            }
            if ( !mapValue.containsKey ( "attributes" ) )
            {
                _log.warn ( "map misses required value 'attributes'" );
                continue;
            }
            if ( !(mapValue.get ( "attributes" ) instanceof MapValue) )
            {
                _log.warn ( "map entry 'attributes' is not of type MapValue" );
                continue;
            }
            
            String type = mapValue.get ( "type" ).toString ();
            Map<String, Variant> attributes = Messages.mapToAttributes ( (MapValue)mapValue.get ( "attributes" ) );
            
            _log.debug ( "entry type: '" + type + "'" );
            
            if ( type.equals ( "folder" ) )
            {
                entry = new FolderEntryCommon ( mapValue.get ( "name" ).toString (), attributes );
            }
            else if ( type.equals ( "item" ) )
            {
                if ( !mapValue.containsKey ( "item-id" ) )
                {
                    _log.warn ( "map entry is an item but misses 'item-id' ");
                    continue;
                }
                if ( !mapValue.containsKey ( "io-direction" ) )
                {
                    _log.warn ( "map entry is an item but misses 'io-direction' ");
                    continue;
                }
                
                String id = mapValue.get ( "item-id" ).toString ();
                
                EnumSet<IODirection> io = Messages.decodeIO ( ValueTools.toInteger ( mapValue.get ( "io-direction" ), 0 ) );
                
                entry = new DataItemEntryCommon ( mapValue.get ( "name" ).toString (), io, attributes, id );
            }
            
            // now add the entry
            if ( entry != null )
                list.add ( entry );
        }
    }
    
    public static Entry [] parseResponse ( Message message )
    {
        List<Entry> list = new ArrayList<Entry> ();
        parseEntries ( message, "entries", list );
        return list.toArray ( new Entry[list.size ()] );
    }
    
    public static void parseEvent ( Message message, List<String> path, List<Entry> added, List<String> removed, Holder<Boolean> full )
    {
        // first clear what we have
        path.clear ();
        added.clear ();
        removed.clear ();
        
        // path 
        if ( message.getValues ().containsKey ( "path" ) )
            if ( message.getValues ().get ( "path" ) instanceof ListValue )
                path.addAll ( ValueTools.fromStringList ( (ListValue)message.getValues ().get ( "path" ) ) );
        
        // added
        parseEntries ( message, "added", added );
        
        // full
        full.value = message.getValues ().containsKey ( "full" );
        
        // removed
        if ( message.getValues ().containsKey ( "removed" ) )
        {
            if ( message.getValues ().get ( "removed" ) instanceof ListValue )
            {
                ListValue listValue = (ListValue)message.getValues ().get ( "removed" );
                for ( Value value : listValue.getValues () )
                {
                    removed.add ( value.toString () );
                }
            }
        }
    }
    
    public static Message createEvent ( String [] path, Collection<Entry> added, Collection<String> removed, boolean full )
    {
        Message message = new Message ( Messages.CC_BROWSER_EVENT );
        
        if ( full )
            message.getValues ().put ( "full", new VoidValue() );
        
        message.getValues ().put ( "path", ValueTools.toStringList ( Arrays.asList ( path ) ) );
        message.getValues().put ( "removed", ValueTools.toStringList ( removed ) );
        
        createEntries ( message, "added", added );
        
        return message;
    }
    
    private static Message createRegMessage ( int commandCode, String [] path )
    {
        Message message = new Message ( commandCode );
        
        message.getValues ().put ( "path", ValueTools.toStringList ( Arrays.asList ( path ) ) );
        
        return message;
    }
    
    public static Message createSubscribe ( String [] path )
    {
        return createRegMessage ( Messages.CC_BROWSER_SUBSCRIBE, path );
    }
    
    public static Message createUnsubscribe ( String [] path )
    {
        return createRegMessage ( Messages.CC_BROWSER_UNSUBSCRIBE, path );
    }
    
    private static String[] parseRegMessage ( Message message )
    {
        if ( !message.getValues ().containsKey ( "path" ) )
            return new String[0];
        if ( !(message.getValues ().get ( "path" ) instanceof ListValue) )
            return new String[0];
        return ValueTools.fromStringList ( (ListValue)message.getValues ().get ( "path" ) ).toArray ( new String[0] );
    }
    
    public static String [] parseSubscribeMessage ( Message message )
    {
        return parseRegMessage ( message );
    }
    
    public static String [] parseUnsubscribeMessage ( Message message )
    {
        return parseRegMessage ( message );
    }
}
