/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.snmp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.ValidationStrategy;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.snmp.utils.MIBManager;
import org.openscada.da.snmp.configuration.ConfigurationPackage;
import org.openscada.da.snmp.configuration.ConfigurationType;
import org.openscada.da.snmp.configuration.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hive extends HiveCommon
{
    private final static Logger logger = LoggerFactory.getLogger ( Hive.class );

    private final Map<String, SNMPNode> nodeMap = new HashMap<String, SNMPNode> ();

    private final FolderCommon rootFolder;

    private MIBManager mibManager;

    public Hive ()
    {
        // create root folder
        this.rootFolder = new FolderCommon ();
        setRootFolder ( this.rootFolder );

        new Thread ( new Runnable () {

            @Override
            public void run ()
            {
                configure ( URI.createFileURI ( "configuration.xml" ) );
            }
        } ).start ();

        setValidatonStrategy ( ValidationStrategy.GRANT_ALL );
    }

    public Hive ( final String uri )
    {
        this ( parse ( URI.createURI ( uri ) ) );
    }

    public Hive ( final ConfigurationType cfg )
    {
        // create root folder
        this.rootFolder = new FolderCommon ();
        setRootFolder ( this.rootFolder );

        new Thread ( new Runnable () {

            @Override
            public void run ()
            {
                configure ( cfg );
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
    protected void configure ( final ConfigurationType cfg )
    {
        this.mibManager = new MIBManager ( cfg.getMibs () );

        for ( final ConnectionType connection : cfg.getConnection () )
        {
            configure ( connection );
        }
    }

    /**
     * configure the hive based on the default config file in the local path
     * 
     * @throws IOException
     */
    protected void configure ( final URI uri )
    {
        final ConfigurationType cfg = parse ( uri );

        configure ( cfg );
    }

    private static ConfigurationType parse ( final URI uri )
    {
        final ResourceSet rs = new ResourceSetImpl ();
        final Resource resource = rs.createResource ( uri );
        try
        {
            resource.load ( null );
        }
        catch ( final IOException e )
        {
            logger.error ( "Failed to load configuration from: " + uri, e );
        }

        final ConfigurationType cfg = (ConfigurationType)EcoreUtil.getObjectByType ( resource.getContents (), ConfigurationPackage.Literals.CONFIGURATION_TYPE );
        return cfg;
    }

    protected void configure ( final ConnectionType connection )
    {
        logger.debug ( "New Connection: {} - {}", connection.getName (), connection.getAddress () );
        ConnectionInformation ci;

        if ( connection.getVersion () == null )
        {
            return;
        }

        switch ( connection.getVersion () )
        {
            case _1:
                ci = new ConnectionInformation ( ConnectionInformation.Version.V1, connection.getName () );
                break;
            case _2:
                ci = new ConnectionInformation ( ConnectionInformation.Version.V2C, connection.getName () );
                break;
            case _3:
                ci = new ConnectionInformation ( ConnectionInformation.Version.V3, connection.getName () );
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
