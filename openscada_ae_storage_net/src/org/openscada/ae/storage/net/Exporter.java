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

package org.openscada.ae.storage.net;

import java.io.IOException;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.multiton.SingleSessionIoHandler;
import org.apache.mina.handler.multiton.SingleSessionIoHandlerDelegate;
import org.apache.mina.handler.multiton.SingleSessionIoHandlerFactory;
import org.openscada.ae.storage.Storage;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.server.net.Server;

public class Exporter
{
    private final Storage storage;

    private Server server;

    private final ConnectionInformation connectionInformation;

    public Exporter ( final Storage storage, final ConnectionInformation connectionInformation ) throws IOException
    {
        this.storage = storage;
        this.connectionInformation = connectionInformation;

        createServer ();
    }

    public Exporter ( final Class<Storage> storageClass, final ConnectionInformation connectionInformation ) throws InstantiationException, IllegalAccessException, IOException
    {
        this.storage = createInstance ( storageClass );
        this.connectionInformation = connectionInformation;

        createServer ();
    }

    public Exporter ( final String storageClassName, final ConnectionInformation connectionInformation ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
    {
        this.storage = createInstance ( Class.forName ( storageClassName ) );
        this.connectionInformation = connectionInformation;

        createServer ();
    }

    private Storage createInstance ( final Class<?> storageClass ) throws InstantiationException, IllegalAccessException
    {
        return (Storage)storageClass.newInstance ();
    }

    private void createServer () throws IOException
    {
        // this._server = new Server ( new ConnectionHandlerServerFactory ( _scheduler, this._storage ), Integer.getInteger ( "openscada.ae.net.server.port", 1302 ) );
        this.server = new Server ( this.connectionInformation );
        this.server.start ( new SingleSessionIoHandlerDelegate ( new SingleSessionIoHandlerFactory () {

            public SingleSessionIoHandler getHandler ( final IoSession session ) throws Exception
            {
                return new ServerConnectionHandler ( Exporter.this.storage, session, Exporter.this.connectionInformation );
            }
        } ) );
    }

    public Class<?> getStorageClass ()
    {
        return this.storage.getClass ();
    }

}
