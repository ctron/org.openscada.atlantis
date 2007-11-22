/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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
package org.openscada.da.server.opc;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.ValidationStrategy;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.timing.Scheduler;
import org.w3c.dom.Node;

public class Hive extends HiveCommon
{
    private static Logger _log = Logger.getLogger ( Hive.class );
    
    private Scheduler _scheduler = null;
    
    private FolderCommon _rootFolderCommon = null;
    
    private DataItemCommand _addCommand = null;
    
    private DataItemCommand _gcCommand = null;

    public Hive () throws XmlException, IOException, ConfigurationError 
    {
        this ( new XMLConfigurator ( "configuration.xml" ) );
    }
    
    public Hive ( Node node ) throws XmlException, IOException, ConfigurationError
    {
        this ( new XMLConfigurator ( node ) );
    }
    
    public Hive ( XMLConfigurator configurator ) throws XmlException, IOException, ConfigurationError
    {
        super ();

        _scheduler = new Scheduler ( true );

        // create root folder
        _rootFolderCommon = new FolderCommon ();
        setRootFolder ( _rootFolderCommon );
        
        // create add command
        _addCommand = new DataItemCommand ( "add" );
        _addCommand.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                try
                {
                    _log.debug ( "Adding connection: " + value.asString ( "" ) );
                    addConnection ( value.asString ( "" ) );
                }
                catch ( Throwable t )
                {
                    _log.warn ( "Failed to add new connection", t );
                }
            }} );
        registerItem ( _addCommand );
        _rootFolderCommon.add ( "add", _addCommand, new MapBuilder<String,Variant> ().put ( "description", new Variant ( "Write a connection string here to temporarily add a new OPC connection." ) ).put ( "sample", new Variant ( "opcda://domain\\user:password@127.0.0.1?clsid=F8582CF2-88FB-11D0-B850-00C0F0104305" ) ).getMap () );
        
        _gcCommand = new DataItemCommand ( "gc" );
        _gcCommand.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                System.gc ();
            }} );
        registerItem ( _gcCommand );
        _rootFolderCommon.add ( "gc", _gcCommand, new MapBuilder<String,Variant> ().put ( "description", new Variant ( "Run the garbage collector once." ) ).getMap () );
        
        setValidatonStrategy ( ValidationStrategy.GRANT_ALL );
        
        configure ( configurator );
    }
    
    public void configure ( XMLConfigurator configurator ) throws ConfigurationError
    {
        if ( configurator != null )
        {
            configurator.configure ( this );
        }
    }
    
    public Scheduler getScheduler ()
    {
        return _scheduler;
    }
    
    public FolderCommon getRootFolderCommon ()
    {
        return _rootFolderCommon;
    }
    
    public void addConnection ( String connectionString )
    {
        Matcher m = Pattern.compile ( "^opcda://(.*?)\\\\(.*?):(.*?)@(.*?)\\?(.*?)=(.*)$" ).matcher ( connectionString );
        
        if ( !m.matches () )
        {
            return;
        }
        
        ConnectionInformation ci = new ConnectionInformation ();
        ci.setDomain ( m.group ( 1 ) );
        ci.setUser ( m.group ( 2 ) );
        ci.setPassword ( m.group ( 3 ) );
        ci.setHost ( m.group ( 4 ) );
        if ( m.group (5 ).toLowerCase().equals ( "clsid" ) )
            ci.setClsid ( m.group ( 6 ) );
        else if ( m.group ( 5 ).toLowerCase ().equals ( "progid" ) )
            ci.setProgId ( m.group ( 6 ) );
        else
            return;
        ConnectionSetup cs = new ConnectionSetup ( ci );
        addConnection ( cs, null, true, new LinkedList<String> () );
    }
    
    public void addConnection ( ConnectionSetup setup, String alias, boolean connect, Collection<String> initialOpcItems )
    {
        OPCConnection connection = new OPCConnection ( this, alias, setup, initialOpcItems );
        
        try
        {
            connection.start ();

            if ( connect )
            {
                connection.triggerConnect ();
            }
        }
        catch (Throwable e )
        {
            _log.error ( "Failed to add connection", e );
        }
        
        
    }
}
