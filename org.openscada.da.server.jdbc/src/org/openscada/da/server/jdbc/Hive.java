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

package org.openscada.da.server.jdbc;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.openscada.da.jdbc.configuration.ConnectionType;
import org.openscada.da.jdbc.configuration.QueryType;
import org.openscada.da.jdbc.configuration.RootDocument;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.ValidationStrategy;
import org.openscada.da.server.common.impl.HiveCommon;
import org.w3c.dom.Node;

public class Hive extends HiveCommon
{
    private static Logger logger = Logger.getLogger ( Hive.class );

    private FolderCommon rootFolder = null;

    private final Collection<Connection> connections = new LinkedList<Connection> ();

    private final Timer timer;

    public Hive () throws XmlException, IOException
    {
        this ( RootDocument.Factory.parse ( new File ( "configuration.xml" ) ) );
    }

    public Hive ( final Node node ) throws XmlException
    {
        this ( RootDocument.Factory.parse ( node ) );
    }

    protected Hive ( final RootDocument doc )
    {
        // create root folder
        this.rootFolder = new FolderCommon ();
        setRootFolder ( this.rootFolder );

        setValidatonStrategy ( ValidationStrategy.GRANT_ALL );

        this.timer = new Timer ( "JdbcHiveTimer", true );

        configure ( doc );

        register ();
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

    private void configure ( final RootDocument doc )
    {
        for ( final ConnectionType connectionType : doc.getRoot ().getConnectionList () )
        {
            createConnection ( connectionType );
        }
    }

    private void createConnection ( final ConnectionType connectionType )
    {
        final Connection connection = new Connection ( connectionType.getId (), connectionType.getTimeout (), connectionType.getConnectionClass (), connectionType.getUri (), connectionType.getUsername (), connectionType.getPassword () );

        for ( final QueryType queryType : connectionType.getQueryList () )
        {
            createQuery ( connection, queryType );
        }

        this.connections.add ( connection );
    }

    private void createQuery ( final Connection connection, final QueryType queryType )
    {
        String sql = queryType.getSql ();
        if ( sql == null || sql.length () == 0 )
        {
            sql = queryType.getSql2 ();
        }

        logger.info ( "Creating new query: " + sql );

        connection.add ( new Query ( queryType.getId (), queryType.getPeriod (), sql, connection ) );
    }
}
