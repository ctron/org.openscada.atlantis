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

package org.openscada.da.server.proxy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlbeans.XmlException;
import org.openscada.core.InvalidOperationException;
import org.openscada.core.Variant;
import org.openscada.da.client.Connection;
import org.openscada.da.proxy.configuration.RootDocument;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;
import org.openscada.da.server.common.impl.HiveCommon;
import org.w3c.dom.Node;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class Hive extends HiveCommon
{
    private final FolderCommon rootFolder;

    private final Map<String, SubConnection> connections = new HashMap<String, SubConnection> ();

    private boolean initialized = false;

    private String separator = ".";

    public Hive () throws XmlException, IOException, ClassNotFoundException
    {
        this ( new XMLConfigurator ( RootDocument.Factory.parse ( new File ( "configuration.xml" ) ) ) );
    }

    public Hive ( final XMLConfigurator configurator ) throws ClassNotFoundException
    {
        this.rootFolder = new FolderCommon ();
        setRootFolder ( this.rootFolder );
        configurator.configure ( this );
    }

    public Hive ( final Node node ) throws XmlException, ClassNotFoundException
    {
        this ( new XMLConfigurator ( RootDocument.Factory.parse ( node ) ) );
    }

    /**
     * @param connection
     */
    public SubConnection addConnection ( final RedundantConnection connection )
    {
        if ( this.initialized )
        {
            throw new IllegalArgumentException ( "no further connections may be added when initialize() was already called!" );
        }
        if ( this.connections.keySet ().contains ( connection.getExposeAs () ) )
        {
            throw new IllegalArgumentException ( "prefix must not already exist!" );
        }
        final SubConnection subConnection = new SubConnection ( connection, connection.getExposeAs (), connection.getExposeAs () );
        this.connections.put ( connection.getExposeAs (), subConnection );
        return subConnection;
    }

    /**
     * @param connection
     */
    public SubConnection addConnection ( final Connection connection, final String prefix )
    {
        if ( this.initialized )
        {
            throw new IllegalArgumentException ( "no further connections may be added when initialize() was already called!" );
        }
        if ( this.connections.keySet ().contains ( prefix ) )
        {
            throw new IllegalArgumentException ( "prefix must not already exist!" );
        }
        final SubConnection subConnection = new SubConnection ( connection, prefix, prefix );
        this.connections.put ( prefix, subConnection );
        return subConnection;
    }

    public void initialize ()
    {
        addConnectionItems ();
        addItemFactory ( new ProxyDataItemFactory ( this.connections, this.separator ) );
        this.initialized = true;
    }

    /**
     * 
     */
    private void addConnectionItems ()
    {
        final FolderCommon connectionsFolder = new FolderCommon ();
        this.rootFolder.add ( "connections", connectionsFolder, new HashMap<String, Variant> () );
        for ( final SubConnection con : this.connections.values () )
        {
            final FolderCommon connectionFolder = new FolderCommon ();
            connectionsFolder.add ( con.getPrefix (), connectionFolder, new HashMap<String, Variant> () );
            if ( RedundantConnection.class.isInstance ( con.getConnection () ) )
            {
                final RedundantConnection redundantConnection = (RedundantConnection)con.getConnection ();
                final WriteHandlerItem connectionIdItem = new WriteHandlerItem ( con.getPrefix () + ".redundant.connection.id", new WriteHandler () {
                    public void handleWrite ( final Variant value ) throws Exception
                    {
                        if ( redundantConnection.isValidConnection ( value.asString () ) )
                        {
                            redundantConnection.switchConnection ( value.asString () );
                        }
                        else
                        {
                            throw new InvalidOperationException ();
                        }
                    }
                } );
                final Map<String, Variant> attr = new HashMap<String, Variant> ();
                for ( final SubConnection subConnection : redundantConnection.getUnderlyingConnections ().values () )
                {
                    attr.put ( "available.connection." + subConnection.getId (), new Variant ( subConnection.getPrefix () ) );
                }
                registerItem ( connectionIdItem );
                connectionFolder.add ( connectionIdItem.getInformation ().getName (), connectionIdItem, new HashMap<String, Variant> () );
                connectionIdItem.updateData ( new Variant ( redundantConnection.getCurrentConnection ().getId () ), attr, AttributeMode.UPDATE );
                redundantConnection.addConnectionChangedListener ( new RedundantConnectionChangedListener () {
                    public void connectionChanged ( final String idOld, final Connection connectionOld, final String idNew, final Connection connectionNew )
                    {
                        connectionIdItem.updateData ( new Variant ( idNew ), null, AttributeMode.UPDATE );
                    }
                } );
            }
        }
    }

    public void setSeparator ( final String separator )
    {
        if ( this.initialized )
        {
            throw new IllegalArgumentException ( "separator may not be changed when initialize() was already called!" );
        }
        this.separator = separator;
    }

    public String getSeparator ()
    {
        return this.separator;
    }
}
