/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.jdbc;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.openscada.da.jdbc.configuration.ColumnMappingType;
import org.openscada.da.jdbc.configuration.ConfigurationPackage;
import org.openscada.da.jdbc.configuration.ConnectionType;
import org.openscada.da.jdbc.configuration.DocumentRoot;
import org.openscada.da.jdbc.configuration.QueryType;
import org.openscada.da.jdbc.configuration.RootType;
import org.openscada.da.jdbc.configuration.UpdateMappingType;
import org.openscada.da.jdbc.configuration.UpdateType;
import org.openscada.da.jdbc.configuration.util.ConfigurationResourceFactoryImpl;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.ValidationStrategy;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.jdbc.Update.Mapping;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hive extends HiveCommon
{

    private final static Logger logger = LoggerFactory.getLogger ( Hive.class );

    private FolderCommon rootFolder = null;

    private final Collection<Connection> connections = new LinkedList<Connection> ();

    private final ScheduledExecutorService timer;

    public Hive () throws IOException
    {
        this ( parse ( URI.createFileURI ( "configuration.xml" ) ) );
    }

    public Hive ( final String uri ) throws IOException
    {
        this ( parse ( URI.createURI ( uri ) ) );
    }

    private static RootType parse ( final URI uri ) throws IOException
    {
        final ResourceSet rs = new ResourceSetImpl ();
        rs.getResourceFactoryRegistry ().getExtensionToFactoryMap ().put ( "*", new ConfigurationResourceFactoryImpl () );

        final Resource r = rs.createResource ( uri );
        r.load ( null );

        final DocumentRoot doc = (DocumentRoot)EcoreUtil.getObjectByType ( r.getContents (), ConfigurationPackage.Literals.DOCUMENT_ROOT );
        if ( doc == null )
        {
            return null;
        }
        else
        {
            return doc.getRoot ();
        }
    }

    public Hive ( final RootType root )
    {
        // create root folder
        this.rootFolder = new FolderCommon ();
        setRootFolder ( this.rootFolder );

        setValidatonStrategy ( ValidationStrategy.GRANT_ALL );

        this.timer = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( "JdbcHiveTimer", true ) );

        configure ( root );

        register ();
    }

    @Override
    public String getHiveId ()
    {
        return "org.openscada.da.server.jdbc";
    }

    @Override
    public void stop () throws Exception
    {
        this.timer.shutdown ();
        super.stop ();
    }

    public void register ()
    {
        for ( final Connection connection : this.connections )
        {
            connection.register ( this, this.rootFolder, this.timer );
        }
    }

    public void unregister ()
    {
        for ( final Connection connection : this.connections )
        {
            connection.unregister ( this );
        }
    }

    private void configure ( final RootType root )
    {
        for ( final ConnectionType connectionType : root.getConnection () )
        {
            createConnection ( connectionType );
        }
    }

    private void createConnection ( final ConnectionType connectionType )
    {
        final Connection connection = new Connection ( connectionType.getId (), connectionType.getTimeout (), connectionType.getConnectionClass (), connectionType.getUri (), connectionType.getUsername (), connectionType.getPassword () );

        for ( final QueryType queryType : connectionType.getQuery () )
        {
            createQuery ( connection, queryType, convertMappings ( queryType.getColumnMapping () ) );
        }

        for ( final UpdateType updateType : connectionType.getUpdate () )
        {
            createUpdate ( connection, updateType );
        }

        this.connections.add ( connection );
    }

    private Map<Integer, String> convertMappings ( final List<ColumnMappingType> list )
    {
        final Map<Integer, String> result = new HashMap<Integer, String> ();

        for ( final ColumnMappingType mapping : list )
        {
            result.put ( mapping.getColumnNumber (), mapping.getAliasName () );
        }

        return result;
    }

    private void createUpdate ( final Connection connection, final UpdateType updateType )
    {
        String sql = updateType.getSql ();
        if ( sql == null || sql.length () == 0 )
        {
            sql = updateType.getSql1 ();
        }

        logger.info ( "Create update: {}", sql );

        final Update update = new Update ( updateType.getId (), sql, connection );

        for ( final UpdateMappingType mappingValue : updateType.getMapping () )
        {
            update.addMapping ( new Mapping ( mappingValue.getName (), mappingValue.getNamedParameter () ) );
        }

        connection.add ( update );
    }

    private void createQuery ( final Connection connection, final QueryType queryType, final Map<Integer, String> columnAliases )
    {
        String sql = queryType.getSql ();
        if ( sql == null || sql.length () == 0 )
        {
            sql = queryType.getSql1 ();
        }

        logger.info ( "Creating new query: {}", sql );

        connection.add ( new Query ( queryType.getId (), queryType.getPeriod (), sql, connection, columnAliases ) );
    }
}
