/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ca.server.ngp;

import java.net.InetSocketAddress;
import java.util.Collection;

import org.apache.mina.core.session.IoSession;
import org.eclipse.scada.protocol.ngp.common.ProtocolConfigurationFactory;
import org.openscada.ca.server.Service;
import org.openscada.core.server.ngp.ServerBase;
import org.openscada.core.server.ngp.ServerConnection;

public class Server extends ServerBase
{

    private final Service service;

    public Server ( final Collection<InetSocketAddress> addresses, final ProtocolConfigurationFactory protocolConfigurationFactory, final Service service ) throws Exception
    {
        super ( addresses, protocolConfigurationFactory );
        this.service = service;
    }

    @Override
    public ServerConnection createNewConnection ( final IoSession session )
    {
        return new ServerConnectionImpl ( session, this.service );
    }

}
