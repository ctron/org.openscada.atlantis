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

package org.openscada.da.client.net.test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.core.client.net.ConnectionBase;
import org.openscada.core.client.net.ConnectionInfo;
import org.openscada.core.client.net.ConnectionStateListener;
import org.openscada.core.client.net.ConnectionBase.State;
import org.openscada.da.client.net.Connection;
import org.openscada.da.client.net.ItemUpdateListener;
import org.openscada.da.core.Location;
import org.openscada.utils.timing.Scheduler;

public class Application
{
    private static Logger _log = Logger.getLogger ( Application.class );
    
    private static boolean _folder = false;
    private static boolean _time = false;
    
    public static void main ( String[] args ) throws IOException
    {
   
        ConnectionInfo info = new ConnectionInfo();
        info.setHostName ( InetAddress.getLocalHost ().getHostAddress () );
        info.setPort ( Integer.getInteger ( "openscada.da.net.server.port", 1202 ) );
        
        final Connection connection = new Connection ( info );
        
        connection.addConnectionStateListener(new ConnectionStateListener() {

            public void stateChange ( ConnectionBase connection, State state, Throwable error )
            {
                if ( error != null )
                    _log.warn ( "State changed to: " + state.toString () + " (" + error.getMessage () + ")" );
                else
                    _log.info ( "State changed to: " + state.toString () );
            }

            });
        
        FolderDumper folderDumper;

        
        folderDumper = new FolderDumper ( connection, new Location ( "test", "storage", "grouping1" ) );
        if ( _folder )
            folderDumper.start ();
        
        if ( _time )
        connection.addItemUpdateListener ( "time", true, new ItemUpdateListener(){

            public void notifyValueChange ( Variant value, boolean initial )
            {
                try
                {
                    _log.debug("Value changed: " + value.asString() );
                }
                catch ( NullValueException e )
                {
                   _log.debug("Value changed to null!");
                }
                
            }

            public void notifyAttributeChange ( Map<String, Variant> attributes, boolean initial )
            {
                // TODO Auto-generated method stub
                
            }});
        
        connection.addItemUpdateListener("memory", true, new ItemUpdateListener(){

            public void notifyValueChange ( Variant value, boolean initial )
            {
                try
                {
                    _log.debug("Value changed: " + value.asString() );
                }
                catch ( NullValueException e )
                {
                   _log.debug("Value changed to null!");
                }
                
            }

            public void notifyAttributeChange ( Map<String, Variant> attributes, boolean initial )
            {
                // TODO Auto-generated method stub
                
            }});
        
        // add a job for writing
        Scheduler scheduler = new Scheduler();
        scheduler.addJob ( new Runnable(){

            public void run ()
            {
                try
                {
                    _log.debug ( "Writing..." );
                    connection.write ( "memory", new Variant("Test: " + System.currentTimeMillis()), new OperationDumpListener () );
                    connection.write ( "command", new Variant(System.currentTimeMillis()), new OperationDumpListener () );
                    _log.debug ( "Writing...complete!" );
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }}, 5*1000, true );
        
        connection.connect ();
        
        while ( true )
        {
            try
            {
                Thread.sleep(1000);
            }
            catch ( InterruptedException e )
            {
            }
        }
    }
}
