/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.eclipse.scada.hd.connection.provider.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.scada.core.ConnectionInformation;
import org.eclipse.scada.core.connection.provider.AbstractConnectionManager;
import org.eclipse.scada.core.connection.provider.AbstractConnectionService;
import org.eclipse.scada.hd.client.Connection;
import org.eclipse.scada.hd.connection.provider.ConnectionService;
import org.eclipse.scada.hd.connection.provider.ConnectionServiceImpl;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionManager extends AbstractConnectionManager
{
    private static final Logger logger = LoggerFactory.getLogger ( ConnectionManager.class );

	private final static Set<String> interfaces;
	
	static
	{
		Set<String> create = new HashSet<String> ();
		create.add ( ConnectionService.class.getName () );
		
		interfaces = Collections.unmodifiableSet ( create ); 
	}
    
    public ConnectionManager ( final BundleContext context, final String connectionId, final ConnectionInformation connectionInformation, final Integer autoReconnectDelay, final boolean initialOpen )
    {
        super ( context, connectionInformation, connectionId, autoReconnectDelay, initialOpen );
    }

    protected AbstractConnectionService createConnection ()
    {
        logger.debug ( "Create new HD connection: {}", getConnectionInformation () );

        final Connection connection = (Connection)getFactory ().getDriverInformation ( getConnectionInformation () ).create ( getConnectionInformation () );

        if ( connection == null )
        {
            logger.warn ( "Failed to create new HD connection: {}", getConnectionInformation () );
            return null;
        }

        return new ConnectionServiceImpl ( connection, getAutoReconnectDelay () );
    }
    
    @Override
    protected Set<String> getInterfaces ()
    {
    	return interfaces;
    }

}
