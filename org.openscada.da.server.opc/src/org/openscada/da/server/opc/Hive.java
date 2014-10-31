/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.opc;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.da.server.common.ValidationStrategy;
import org.eclipse.scada.da.server.common.impl.HiveCommon;
import org.jinterop.dcom.common.JISystem;
import org.openscada.da.opc.configuration.ConfigurationPackage;
import org.openscada.da.opc.configuration.RootType;
import org.openscada.da.opc.configuration.util.ConfigurationResourceFactoryImpl;
import org.openscada.da.server.opc.configuration.XMLConfigurator;
import org.openscada.da.server.opc.connection.OPCConnection;
import org.openscada.da.server.opc.connection.data.ConnectionSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hive extends HiveCommon
{

    private final static Logger logger = LoggerFactory.getLogger ( Hive.class );

    private final Collection<OPCConnection> connections = new CopyOnWriteArrayList<OPCConnection> ();

    private final FolderCommon rootFolder = new FolderCommon ();

    private final XMLConfigurator configurator;

    public Hive () throws IOException
    {
        this ( new XMLConfigurator ( parse ( URI.createFileURI ( System.getProperty ( "org.openscada.da.server.opc.defaultConfigurationFile", "configuration.xml" ) ) ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public Hive ( final String uri ) throws IOException
    {
        this ( new XMLConfigurator ( parse ( URI.createURI ( uri ) ) ) );
    }

    public Hive ( final RootType root )
    {
        this ( new XMLConfigurator ( root ) );
    }

    public Hive ( final XMLConfigurator configurator )
    {
        initJInterop ();

        setValidatonStrategy ( ValidationStrategy.GRANT_ALL );

        setRootFolder ( this.rootFolder );
        this.configurator = configurator;
    }

    @Override
    protected void performStart () throws Exception
    {
        super.performStart ();

        this.configurator.configure ( this );
    }

    @Override
    protected void performStop () throws Exception
    {
        for ( final OPCConnection connection : this.connections )
        {
            try
            {
                connection.dispose ();
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed to clean up connection", e );
            }
        }
        super.performStop ();
    }

    private static RootType parse ( final URI uri ) throws IOException
    {
        ConfigurationPackage.eINSTANCE.eClass ();

        final ResourceSetImpl rs = new ResourceSetImpl ();
        rs.getResourceFactoryRegistry ().getExtensionToFactoryMap ().put ( "*", new ConfigurationResourceFactoryImpl () );

        final Resource r = rs.createResource ( uri );
        r.load ( null );

        return (RootType)EcoreUtil.getObjectByType ( r.getContents (), ConfigurationPackage.Literals.ROOT_TYPE );
    }

    @Override
    public String getHiveId ()
    {
        return "org.openscada.da.server.opc"; //$NON-NLS-1$
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

    public void addConnection ( final ConnectionSetup setup, final boolean connect )
    {
        final OPCConnection connection = new OPCConnection ( this, this.rootFolder, setup );

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
