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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.snmp.ConfigurationDocument.Configuration;
import org.openscada.da.server.snmp.ConfigurationDocument.Configuration.Connection;

public class Hive extends HiveCommon
{
    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger ( Hive.class );
    
    private Map<String, SNMPNode> _nodeMap = new HashMap<String, SNMPNode> ();
    
    private FolderCommon _rootFolder = null;
    
	public Hive ()
	{
		super();
		
        // create root folder
		_rootFolder = new FolderCommon ();
        setRootFolder ( _rootFolder );

        new Thread ( new Runnable () {

            public void run ()
            {
                configure ();
            }} ).start ();
	}
    
    private void configure ()
    {
        File configurationFile = new File ( "configuration.xml" );
        try
        {
            ConfigurationDocument doc = ConfigurationDocument.Factory.parse ( configurationFile );
            for ( Configuration.Connection connection : doc.getConfiguration ().getConnectionList () )
            {
                configureConnection ( connection );
            }
        }
        catch ( XmlException e )
        {
            _log.warn ( "Unable to configure hive", e );
        }
        catch ( IOException e )
        {
            _log.warn ( "Unable to configure hive", e );
        }
    }

    private void configureConnection ( Connection connection )
    {
        _log.debug ( String.format ( "New Connection: %1$s - %2$s", connection.getName (), connection.getAddress () ) );
        ConnectionInformation ci;
        
        switch ( connection.getVersion () )
        {
        case 1:
            ci = new ConnectionInformation ( ConnectionInformation.Version.V1, connection.getName () );
            break;
        case 2:
            ci = new ConnectionInformation ( ConnectionInformation.Version.V2C, connection.getName () );
            break;
        default:
            return;
        }

        ci.setAddress ( connection.getAddress () );
        ci.setCommunity ( connection.getCommunity () );
        
        SNMPNode node = new SNMPNode ( this, _rootFolder, ci );
        node.register ();
        _nodeMap.put ( connection.getName (), node );
    }
}
