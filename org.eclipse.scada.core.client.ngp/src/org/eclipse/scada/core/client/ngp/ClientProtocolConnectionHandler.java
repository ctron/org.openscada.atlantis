/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.eclipse.scada.core.client.ngp;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.eclipse.scada.core.client.common.ClientBaseConnection;
import org.eclipse.scada.core.client.common.ClientConnectionHandler;
import org.eclipse.scada.protocol.ngp.common.ProtocolConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientProtocolConnectionHandler extends ClientConnectionHandler implements IoHandler
{
    final static Logger logger = LoggerFactory.getLogger ( ClientProtocolConnectionHandler.class );

    private final ProtocolConfiguration protocolConfiguration;

    public ClientProtocolConnectionHandler ( final ClientBaseConnection connection, final ProtocolConfiguration protocolConfiguration )
    {
        super ( connection );
        this.protocolConfiguration = protocolConfiguration;
    }

    @Override
    public void sessionCreated ( final IoSession session ) throws Exception
    {
        this.protocolConfiguration.assign ( session );
    }

}
