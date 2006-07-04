/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.snmp;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.da.core.browser.common.FolderCommon;
import org.openscada.da.core.common.impl.HiveCommon;

public class Hive extends HiveCommon
{
    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger ( Hive.class );
    
    private Map<String, SNMPNode> _nodeMap = new HashMap<String, SNMPNode> ();
    
	public Hive ()
	{
		super();
		
        // create root folder
		FolderCommon rootFolder = new FolderCommon ();
        setRootFolder ( rootFolder );

        SNMPNode node;
        
        ConnectionInformation connectionInformation = new ConnectionInformation ( "localhost" );
        connectionInformation.setAddress ( "udp:127.0.0.1/161" );
        connectionInformation.setCommunity ( "public" );
        
        node = new SNMPNode ( this, rootFolder, connectionInformation );
        node.register ();
        _nodeMap.put ( "localhost", node );

	}
}
