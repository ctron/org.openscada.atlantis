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

package org.openscada.ae.storage.syslog;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.openscada.ae.core.Event;
import org.openscada.ae.core.QueryDescription;
import org.openscada.ae.storage.common.StorageCommon;
import org.openscada.ae.storage.common.memory.MemoryQuery;
import org.openscada.ae.storage.syslog.provider.SyslogDaemonProvider;
import org.openscada.ae.storage.syslog.provider.SyslogFileProvider;
import org.openscada.ae.syslog.ConfigurationDocument;
import org.openscada.ae.syslog.FileProviderType;
import org.openscada.ae.syslog.SyslogUdpProviderType;
import org.openscada.core.Variant;
import org.openscada.utils.collection.MapBuilder;

public class Storage extends StorageCommon implements DataStore
{
    private static Logger _log = Logger.getLogger ( Storage.class );
    
    private MemoryQuery _allQuery = new MemoryQuery ();
    
    private List<Object> _providers = new LinkedList<Object> ();
    private SyslogClient _client = null;
    
    public Storage () throws XmlException, IOException
    {
        super ();
        
        addQuery ( new QueryDescription ( "all", new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "A query containing all items" ) )
                .getMap () ), _allQuery );
    
        configure ();
        
        _client = new SyslogClient ( new InetSocketAddress ( "localhost", 514 ) );
        
        Event event = new Event ( "" );
        event.getAttributes ().put ( "message", new Variant ( "Hello World" ) );
        event.getAttributes ().put ( "application", new Variant ( "test" ) );
        try
        {
            submitEvent ( new Properties (), event );
        }
        catch ( Exception e )
        {
            _log.debug ( "Failed to send hello world", e );
        }
    }
    
    private void configure () throws XmlException, IOException
    {
        String fileName = System.getProperty ( "org.openscada.ae.syslog.configuration", "configuration.xml" );
        
        File file = new File ( fileName );
        try
        {
            ConfigurationDocument doc = ConfigurationDocument.Factory.parse ( file );
            for ( FileProviderType fileProvider : doc.getConfiguration ().getProviders ().getFileProviderList () )
            {
                _providers.add ( new SyslogFileProvider ( this, new File ( fileProvider.getFile () ), fileProvider.getDefaultSeverity ().toString () ) );
            }
            for ( SyslogUdpProviderType udpProvider : doc.getConfiguration ().getProviders ().getSyslogUdpProviderList () )
            {
                _providers.add ( new SyslogDaemonProvider ( this, udpProvider.getPort () ) );
            }

        }
        catch ( Exception e )
        {
            _log.warn ( "Unable to open configuration", e );
        }
    }

    @Override
    public synchronized void submitEvent ( Properties properties, Event event ) throws Exception
    {
        if ( _client == null )
            throw new Exception ( "Instance cannot handle submitted messages" );
        
        if ( !event.getAttributes ().containsKey ( "message" ) )
            throw new Exception ( "Can only handle events with 'message' attribute" );
        
        if ( !event.getAttributes ().containsKey ( "application" ) )
            throw new Exception ( "Can only handle events with 'application' attribute" );
        
        SyslogMessage message = new SyslogMessage ();
        
        message.setMessage ( event.getAttributes ().get ( "message" ).asString ( "" ) );
        message.setApplication ( event.getAttributes ().get ( "application" ).asString ( "openscada" ) );
        
        // process id
        Variant pid = event.getAttributes ().get ( "pid" );
        if ( pid != null )
            if ( pid.isInteger () || pid.isLong () )
                message.setProcessId ( pid.asLong () );
        
        // hostname
        if ( event.getAttributes ().containsKey ( "host" ) )
            message.setHost ( event.getAttributes ().get ( "host" ).asString ( "localhost" ) );
        
        if ( event.getAttributes ().containsKey ( "severity" ) )
        {
            String severity = event.getAttributes ().get ( "severity" ).asString ( "" ).toUpperCase ();
            if ( severity.equals ( "FATAL" ) )
                message.setPriority ( SyslogMessage.Priority.ALERT );
            else if ( severity.equals ( "ERROR" ) )
                message.setPriority ( SyslogMessage.Priority.ERROR );
            else if ( severity.equals ( "WARNING" ) )
                message.setPriority ( SyslogMessage.Priority.WARNING );
            else if ( severity.equals ( "INFO" ) )
                message.setPriority ( SyslogMessage.Priority.INFO );
            else if ( severity.equals ( "DEBUG " ) )
                message.setPriority ( SyslogMessage.Priority.DEBUG );
            else
                message.setPriority ( SyslogMessage.Priority.INFO );
        }
        
        message.setTimestamp ( event.getTimestamp () );
        
        _client.sendMessage ( message );
    }
    
    public void submitEvent ( Event event )
    {
        try
        {
            super.submitEvent ( new Properties (), event );
        }
        catch ( Exception e )
        {
         _log.warn ( "failed to add event", e );   
        }
    }
}
