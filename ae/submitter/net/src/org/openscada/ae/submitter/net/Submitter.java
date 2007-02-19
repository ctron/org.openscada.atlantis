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

package org.openscada.ae.submitter.net;

import java.util.Properties;

import org.openscada.ae.core.Event;
import org.openscada.ae.core.Submission;
import org.openscada.core.client.ConnectWaitController;
import org.openscada.core.client.net.ConnectionInfo;

public class Submitter implements Submission
{
    
    private ConnectionInfo _connectionInformation = null;
    private Connection _connection = null;
    
    public Submitter ()
    {
        super ();
        
        _connectionInformation = new ConnectionInfo ();
        _connectionInformation.setAutoReconnect ( false );
        _connectionInformation.setHostName ( System.getProperty ( "openscada.ae.submitter.net.hostname" ) );
        _connectionInformation.setPort ( Integer.getInteger ( "openscada.ae.submitter.net.port", 1302 ) );
    }
    
    public Submitter ( ConnectionInfo connectionInfo )
    {
        super ();
        
        _connectionInformation = connectionInfo;
    }
    
    synchronized protected Connection getConnection () throws Throwable
    {
        if ( _connection == null )
        {
            _connection = new Connection ( _connectionInformation );
            new ConnectWaitController ( _connection ).connect ();
        }
        return _connection;
    }
    
    public void submitEvent ( Properties properties, Event event ) throws Throwable
    {
        getConnection ().submitEvent ( properties, event );
    }
    
}
