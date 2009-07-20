/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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
import org.openscada.da.server.common.ValidationStrategy;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.snmp.utils.MIBManager;
import org.openscada.da.snmp.configuration.ConfigurationDocument;
import org.openscada.da.snmp.configuration.ConfigurationDocument.Configuration.Connection;
import org.w3c.dom.Node;

public class Hive extends HiveCommon
{
    private static Logger logger = Logger.getLogger ( Hive.class );

    private final Map<String, SNMPNode> nodeMap = new HashMap<String, SNMPNode> ();

    private FolderCommon rootFolder = null;

    private MIBManager mibManager;

    public Hive ()
    {
        super ();

        // create root folder
        this.rootFolder = new FolderCommon ();
        setRootFolder ( this.rootFolder );

        new Thread ( new Runnable () {

            public void run ()
            {
                configure ();
            }
        } ).start ();

        setValidatonStrategy ( ValidationStrategy.GRANT_ALL );
    }

    public Hive ( final Node node )
    {
        super ();

        // create root folder
        this.rootFolder = new FolderCommon ();
        setRootFolder ( this.rootFolder );

        new Thread ( new Runnable () {

            public void run ()
            {
                configure ( node );
            }
        } ).start ();

        setValidatonStrategy ( ValidationStrategy.GRANT_ALL );
    }

    /**
     * configure the hive based on a configuration document
     * @param doc
     */
    protected void configure ( final ConfigurationDocument doc )
    {
        this.mibManager = new MIBManager ( doc.getConfiguration ().getMibs () );

        for ( final Connection connection : doc.getConfiguration ().getConnectionList () )
        {
            configure ( connection );
        }
    }

    /**
     * configure the hive based on a anonymous xml node
     * @param node the xml node which must contain an xml tree of the configuration schema
     */
    protected void configure ( final Node node )
    {
        try
        {
            configure ( ConfigurationDocument.Factory.parse ( node ) );
        }
        catch ( final XmlException e )
        {
            logger.warn ( "Unable to configure hive", e );
        }
    }

    /**
     * configure the hive based on the default config file in the local path
     */
    protected void configure ()
    {
        try
        {
            configure ( ConfigurationDocument.Factory.parse ( new File ( "configuration.xml" ) ) );
        }
        catch ( final XmlException e )
        {
            logger.warn ( "Unable to configure hive", e );
        }
        catch ( final IOException e )
        {
            logger.warn ( "Unable to configure hive", e );
        }
    }

    protected void configure ( final Connection connection )
    {
        logger.debug ( String.format ( "New Connection: %1$s - %2$s", connection.getName (), connection.getAddress () ) );
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

        final SNMPNode node = new SNMPNode ( this, this.rootFolder, this.mibManager, ci );
        node.register ();
        this.nodeMap.put ( connection.getName (), node );
    }
}
