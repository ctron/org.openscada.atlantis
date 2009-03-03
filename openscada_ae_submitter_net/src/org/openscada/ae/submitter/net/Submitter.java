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

package org.openscada.ae.submitter.net;

import java.util.Properties;

import org.openscada.ae.core.Event;
import org.openscada.ae.core.Submission;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.ConnectWaitController;

public class Submitter implements Submission
{
    private Connection connection = null;

    private final ConnectionInformation connectionInformation;

    public Submitter ()
    {
        this ( ConnectionInformation.fromURI ( "da:aes://" + System.getProperty ( "openscada.ae.submitter.net.hostname" ) + ":" + Integer.getInteger ( "openscada.ae.submitter.net.port", 1302 ) ) );
    }

    public Submitter ( final ConnectionInformation connectionInfo )
    {
        super ();

        this.connectionInformation = connectionInfo;
    }

    synchronized protected Connection getConnection () throws Throwable
    {
        if ( this.connection == null )
        {
            this.connection = new Connection ( this.connectionInformation );
            new ConnectWaitController ( this.connection ).connect ();
        }
        return this.connection;
    }

    public void submitEvent ( final Properties properties, final Event event ) throws Throwable
    {
        getConnection ().submitEvent ( properties, event );
    }

}
