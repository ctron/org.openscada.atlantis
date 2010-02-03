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

import java.io.IOException;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.multiton.SingleSessionIoHandler;
import org.apache.mina.handler.multiton.SingleSessionIoHandlerDelegate;
import org.apache.mina.handler.multiton.SingleSessionIoHandlerFactory;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.server.net.Server;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.common.impl.ExporterBase;

public class Exporter extends ExporterBase
{
    private Server server;

    public Exporter ( final Hive hive, final ConnectionInformation connectionInformation ) throws Exception
    {
        super ( hive, connectionInformation );
    }

    @Deprecated
    public Exporter ( final Class<?> hiveClass, final ConnectionInformation connectionInformation ) throws Exception
    {
        super ( hiveClass, connectionInformation );
    }

    @Deprecated
    public Exporter ( final String hiveClassName, final ConnectionInformation connectionInformation ) throws Exception
    {
        super ( hiveClassName, connectionInformation );
    }

    private void createServer () throws IOException
    {
        this.server = new Server ( this.connectionInformation );
        this.server.start ( createFactory () );
    }

    private SingleSessionIoHandlerDelegate createFactory ()
    {
        return new SingleSessionIoHandlerDelegate ( new SingleSessionIoHandlerFactory () {

            public SingleSessionIoHandler getHandler ( final IoSession session ) throws Exception
            {
                return new ServerConnectionHandler ( Exporter.this.hive, session, Exporter.this.connectionInformation );
            }
        } );
    }

    public void start () throws Exception
    {
        createServer ();
    }

    public void stop () throws Exception
    {
        destroyServer ();
    }

    private void destroyServer ()
    {
        if ( this.server != null )
        {
            this.server.dispose ();
            this.server = null;
        }
    }
}
