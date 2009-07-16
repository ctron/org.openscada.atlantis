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

package org.openscada.da.server.opc;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.jinterop.dcom.common.JISystem;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.ValidationStrategy;
import org.openscada.da.server.common.chain.storage.ChainStorageServiceHelper;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.opc.configuration.XMLConfigurator;
import org.openscada.da.server.opc.connection.ConnectionSetup;
import org.openscada.da.server.opc.connection.OPCConnection;
import org.openscada.da.server.opc.preload.ItemSource;
import org.w3c.dom.Node;

public class Hive extends HiveCommon
{
    private static Logger logger = Logger.getLogger ( Hive.class );

    private final Collection<OPCConnection> connections = new CopyOnWriteArrayList<OPCConnection> ();

    private final FolderCommon rootFolder = new FolderCommon ();

    public Hive () throws XmlException, IOException, ConfigurationError
    {
        this ( new XMLConfigurator ( "configuration.xml" ) );
    }

    public Hive ( final Node node ) throws XmlException, IOException, ConfigurationError
    {
        this ( new XMLConfigurator ( node ) );
    }

    public Hive ( final XMLConfigurator configurator ) throws XmlException, IOException, ConfigurationError
    {
        super ();

        initJInterop ();

        // enable chain storage for this hive
        ChainStorageServiceHelper.registerDefaultPropertyService ( this );

        setValidatonStrategy ( ValidationStrategy.GRANT_ALL );

        setRootFolder ( this.rootFolder );

        configurator.configure ( this );
    }

    private void initJInterop ()
    {
        logger.warn ( String.format ( "rpc.socketTimeout = %s", System.getProperty ( "rpc.socketTimeout" ) ) );
        if ( !Boolean.getBoolean ( "dcom.debug" ) )
        {
            java.util.logging.Logger.getLogger ( "org.jinterop" ).setLevel ( Level.WARNING );
        }
        JISystem.setJavaCoClassAutoCollection ( !Boolean.getBoolean ( "dcom.disableAutoCollection" ) );
        logger.info ( "DCOM auto collection: " + JISystem.isJavaCoClassAutoCollectionSet () );
    }

    public void addConnection ( final ConnectionSetup setup, final boolean connect, final Collection<ItemSource> itemSources )
    {
        final OPCConnection connection = new OPCConnection ( this, this.rootFolder, setup, itemSources );

        if ( this.connections.add ( connection ) )
        {
            connection.start ();
            if ( connect )
            {
                // initially connect
                connection.connect ();
            }
        }
    }

    public void removeConnection ( final OPCConnection connection )
    {
        if ( this.connections.remove ( connection ) )
        {
            connection.dispose ();
        }
    }

}
