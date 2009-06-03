/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.proxy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.proxy.configuration.RootDocument;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.chain.storage.ChainStorageServiceHelper;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.proxy.configuration.XMLConfigurator;
import org.openscada.da.server.proxy.connection.ProxyConnection;
import org.openscada.da.server.proxy.utils.ProxyPrefixName;
import org.w3c.dom.Node;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class Hive extends HiveCommon
{
    private final static Logger logger = Logger.getLogger ( Hive.class );

    private final FolderCommon rootFolder;

    private final Map<ProxyPrefixName, ProxyConnection> connections = new HashMap<ProxyPrefixName, ProxyConnection> ();

    private boolean initialized = false;

    private String separator = ".";

    private FolderCommon connectionsFolder;

    private final XMLConfigurator configurator;

    /**
     * @throws XmlException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NotConvertableException 
     * @throws NullValueException 
     * @throws InvalidOperationException 
     * @throws ConfigurationError 
     */
    public Hive () throws XmlException, IOException, ClassNotFoundException, InvalidOperationException, NullValueException, NotConvertableException, ConfigurationError
    {
        this ( new XMLConfigurator ( RootDocument.Factory.parse ( new File ( "configuration.xml" ) ) ) );
    }

    /**
     * @param configurator
     * @throws ClassNotFoundException
     * @throws NotConvertableException 
     * @throws NullValueException 
     * @throws InvalidOperationException 
     * @throws ConfigurationError 
     */
    public Hive ( final XMLConfigurator configurator ) throws ClassNotFoundException, InvalidOperationException, NullValueException, NotConvertableException, ConfigurationError
    {
        // enable chain storage for this hive
        ChainStorageServiceHelper.registerDefaultPropertyService ( this );

        this.configurator = configurator;

        this.rootFolder = new FolderCommon ();
        setRootFolder ( this.rootFolder );
    }

    /**
     * @param node
     * @throws XmlException
     * @throws ClassNotFoundException
     * @throws NotConvertableException 
     * @throws NullValueException 
     * @throws InvalidOperationException 
     * @throws ConfigurationError 
     */
    public Hive ( final Node node ) throws XmlException, ClassNotFoundException, InvalidOperationException, NullValueException, NotConvertableException, ConfigurationError
    {
        this ( new XMLConfigurator ( RootDocument.Factory.parse ( node ) ) );
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
     * 
     */
    @Override
    public void start () throws Exception
    {
        super.start ();

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
    public void stop () throws Exception
    {
        for ( final ProxyConnection connection : this.connections.values () )
        {
            connection.stop ();
        }
        super.stop ();
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
