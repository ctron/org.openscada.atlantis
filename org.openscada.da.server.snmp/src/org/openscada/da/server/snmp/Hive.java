/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.snmp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlbeans.XmlException;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.ValidationStrategy;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.snmp.utils.MIBManager;
import org.openscada.da.snmp.configuration.ConfigurationDocument;
import org.openscada.da.snmp.configuration.ConfigurationDocument.Configuration.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class Hive extends HiveCommon
{
    private final static Logger logger = LoggerFactory.getLogger ( Hive.class );

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

            @Override
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

            @Override
            public void run ()
            {
                configure ( node );
            }
        } ).start ();

        setValidatonStrategy ( ValidationStrategy.GRANT_ALL );
    }

    @Override
    public String getHiveId ()
    {
        return "org.openscada.da.server.snmp";
    }

    /**
     * configure the hive based on a configuration document
     * 
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
     * 
     * @param node
     *            the xml node which must contain an xml tree of the
     *            configuration schema
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
        logger.debug ( "New Connection: {} - {}", connection.getName (), connection.getAddress () );
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
