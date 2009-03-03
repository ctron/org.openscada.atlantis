/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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
import org.openscada.core.net.OperationController;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.BrowseOperationListener;
import org.openscada.da.core.server.Hive;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.browser.HiveBrowser;
import org.openscada.net.base.data.Message;
import org.openscada.net.da.handler.ListBrowser;
import org.openscada.net.mina.Messenger;

public class BrowseController extends OperationController implements BrowseOperationListener
{
    private Hive hive = null;

    private Session session = null;

    private Messenger messenger = null;

    private Long id = null;

    public BrowseController ( final Hive hive, final Session session, final Messenger messenger )
    {
        super ( messenger );
        this.hive = hive;
        this.session = session;
        this.messenger = messenger;
    }

    public void run ( final Message request )
    {
        try
        {
            final String[] location = ListBrowser.parseRequest ( request );

            final HiveBrowser browser = this.hive.getBrowser ();
            if ( browser == null )
            {
                throw new Exception ( "Browsing not supported" );
            }
            this.id = browser.startBrowse ( this.session, new Location ( location ), this );
        }
        catch ( final Exception e )
        {
            sendFailure ( request, e );
            return;
        }

        // send out ACK with operation id
        sendACK ( request, this.id );

        try
        {
            this.hive.thawOperation ( this.session, this.id );
        }
        catch ( final InvalidSessionException e )
        {
            // should never happen
        }
    }

    public void failure ( final Throwable throwable )
    {
        this.messenger.sendMessage ( ListBrowser.createResponse ( this.id, throwable.getMessage () ) );
    }

    public void success ( final Entry[] entry )
    {
        this.messenger.sendMessage ( ListBrowser.createResponse ( this.id, entry ) );
    }
}
