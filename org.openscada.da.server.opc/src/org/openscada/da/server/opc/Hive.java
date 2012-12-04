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

package org.openscada.da.server.opc;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class Hive extends HiveCommon
{

    private final static Logger logger = LoggerFactory.getLogger ( Hive.class );

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

    @Override
    public String getHiveId ()
    {
        return "org.openscada.da.server.opc";
    }

    private void initJInterop ()
    {
        logger.warn ( "rpc.socketTimeout = {}", System.getProperty ( "rpc.socketTimeout" ) );
        if ( !Boolean.getBoolean ( "dcom.debug" ) )
        {
            java.util.logging.Logger.getLogger ( "org.jinterop" ).setLevel ( Level.WARNING );
        }
        JISystem.setJavaCoClassAutoCollection ( !Boolean.getBoolean ( "dcom.disableAutoCollection" ) );
        logger.info ( "DCOM auto collection: {}", JISystem.isJavaCoClassAutoCollectionSet () );
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
