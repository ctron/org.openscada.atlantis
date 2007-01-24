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

package org.openscada.da.server.net;

import org.openscada.core.InvalidSessionException;
import org.openscada.core.Variant;
import org.openscada.core.net.OperationController;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.BrowseOperationListener;
import org.openscada.da.core.server.Hive;
import org.openscada.da.core.server.InvalidItemException;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.WriteOperationListener;
import org.openscada.da.core.server.browser.HiveBrowser;
import org.openscada.net.base.ConnectionHandlerBase;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.da.handler.ListBrowser;
import org.openscada.net.da.handler.Messages;
import org.openscada.utils.lang.Holder;

public class BrowseController extends OperationController implements BrowseOperationListener
{
    private Hive _hive = null;
    private Session _session = null;
    private ConnectionHandlerBase _connection = null;
    
    private Long _id = null; 
    
    public BrowseController ( Hive hive, Session session, ConnectionHandlerBase connection )
    {
        super ( connection );
        _hive = hive;
        _session = session;
        _connection = connection;
    }
    
    public void run ( Message request )
    {
        try
        {
            String [] location = ListBrowser.parseRequest ( request );
            
            HiveBrowser browser = _hive.getBrowser ();
            if ( browser == null )
            {
                throw new Exception ( "Browsing not supported" );
            }
            _id = browser.startBrowse ( _session, new Location ( location ), this );
        }
        catch ( Exception e )
        {
            sendFailure ( request, e );
            return;
        }
        
        // send out ACK with operation id
        sendACK ( request, _id );
        
        try
        {
            _hive.thawOperation ( _session, _id );
        }
        catch ( InvalidSessionException e )
        {
            // should never happen
        }
    }
    
    public void failure ( Throwable throwable )
    {
        _connection.getConnection ().sendMessage ( ListBrowser.createResponse ( _id, throwable.getMessage () ) );
    }

    public void success ( Entry[] entry )
    {
        _connection.getConnection ().sendMessage ( ListBrowser.createResponse ( _id, entry ) );
    }
}
