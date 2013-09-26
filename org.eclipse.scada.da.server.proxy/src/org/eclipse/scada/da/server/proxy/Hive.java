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

package org.eclipse.scada.da.server.proxy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.proxy.configuration.ConfigurationPackage;
import org.eclipse.scada.da.proxy.configuration.RootType;
import org.eclipse.scada.da.proxy.configuration.util.ConfigurationResourceFactoryImpl;
import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.da.server.proxy.configuration.XMLConfigurator;
import org.eclipse.scada.da.server.proxy.connection.ProxyConnection;
import org.eclipse.scada.da.server.proxy.utils.ProxyPrefixName;
import org.openscada.da.server.common.impl.HiveCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Juergen Rose &lt;juergen.rose@th4-systems.com&gt;
 */
public class Hive extends HiveCommon
{

    private final static Logger logger = LoggerFactory.getLogger ( Hive.class );

    private final FolderCommon rootFolder;

    private final Map<ProxyPrefixName, ProxyConnection> connections = new HashMap<ProxyPrefixName, ProxyConnection> ();

    private boolean initialized = false;

    private String separator = ".";

    private FolderCommon connectionsFolder;

    private final XMLConfigurator configurator;

    public Hive () throws IOException
    {
        this ( new XMLConfigurator ( parse ( URI.createFileURI ( "configuration.xml" ) ) ) );
    }

    public Hive ( final String uri ) throws IOException
    {
        this ( new XMLConfigurator ( parse ( URI.createURI ( uri ) ) ) );
    }

    public Hive ( final XMLConfigurator configurator )
    {
        this.configurator = configurator;

        this.rootFolder = new FolderCommon ();
        setRootFolder ( this.rootFolder );
    }

    public Hive ( final RootType root ) throws Exception
    {
        this ( new XMLConfigurator ( root ) );
    }

    private static RootType parse ( final URI uri ) throws IOException
    {
        final ResourceSet rs = new ResourceSetImpl ();

        rs.getResourceFactoryRegistry ().getExtensionToFactoryMap ().put ( "*", new ConfigurationResourceFactoryImpl () );
        final Resource r = rs.createResource ( uri );
        r.load ( null );

        return (RootType)EcoreUtil.getObjectByType ( r.getContents (), ConfigurationPackage.Literals.ROOT_TYPE );
    }

    @Override
    public String getHiveId ()
    {
        return "org.eclipse.scada.da.server.proxy";
    }

    /**
     * @param group
     * @return
     */
    public ProxyConnection addConnection ( final ProxyPrefixName prefix )
    {
        if ( this.initialized )
        {
            throw new IllegalArgumentException ( "no further connections may be added when initialize() was already called!" );
        }
        if ( this.connections.keySet ().contains ( prefix ) )
        {
            throw new IllegalArgumentException ( "prefix must not already exist!" );
        }

        final ProxyConnection connection = new ProxyConnection ( this, prefix, this.connectionsFolder );
        this.connections.put ( prefix, connection );
        return connection;
    }

    /**
     * @param configurator
     * @throws Exception
     */
    @Override
    protected void performStart () throws Exception
    {
        super.performStart ();

        logger.info ( "Starting hive" );

        // create connections folder
        this.connectionsFolder = new FolderCommon ();
        this.rootFolder.add ( "connections", this.connectionsFolder, new HashMap<String, Variant> () );

        if ( this.configurator != null )
        {
            this.configurator.configure ( this );
        }

        for ( final ProxyConnection connection : this.connections.values () )
        {
            connection.start ();
        }

        // addItemFactory ( new ProxyDataItemFactory ( this.connections, this.separator ) );

        this.initialized = true;
    }

    @Override
    protected void performStop () throws Exception
    {
        for ( final ProxyConnection connection : this.connections.values () )
        {
            connection.stop ();
        }
        super.performStop ();
    }

    /**
     * @param separator
     */
    public void setSeparator ( final String separator )
    {
        if ( this.initialized )
        {
            throw new IllegalArgumentException ( "separator may not be changed when initialize() was already called!" );
        }
        this.separator = separator;
    }

    /**
     * @return separator which separates prefix from rest of item name
     */
    public String getSeparator ()
    {
        return this.separator;
    }
}
