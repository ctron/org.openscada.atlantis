/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.opc.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.xmlbeans.XmlException;
import org.openscada.da.opc.configuration.ConfigurationType;
import org.openscada.da.opc.configuration.RootDocument;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.da.server.opc.Hive;
import org.openscada.da.server.opc.connection.AccessMethod;
import org.openscada.da.server.opc.connection.ConnectionSetup;
import org.openscada.da.server.opc.preload.FileXMLItemSource;
import org.openscada.da.server.opc.preload.InMemoryAbstractItemSource;
import org.openscada.da.server.opc.preload.ItemSource;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.w3c.dom.Node;

public class XMLConfigurator
{

    private static final int DEFAULT_RECONNECT_DELAY = 5000;

    private RootDocument rootDocument = null;

    public XMLConfigurator ( final RootDocument rootDocument )
    {
        this.rootDocument = rootDocument;
    }

    public XMLConfigurator ( final Node node ) throws XmlException
    {
        this ( RootDocument.Factory.parse ( node ) );
    }

    public XMLConfigurator ( final String filename ) throws XmlException, IOException
    {
        this ( RootDocument.Factory.parse ( new File ( filename ) ) );
    }

    public void configure ( final Hive hive ) throws ConfigurationError
    {
        // first configure the base hive
        new org.openscada.da.server.common.configuration.xml.XMLConfigurator ( null, this.rootDocument.getRoot ().getItemTemplates (), null, null ).configure ( hive );

        // now configure the opc hive
        for ( final ConfigurationType configuration : this.rootDocument.getRoot ().getConnections ().getConfigurationList () )
        {
            if ( !configuration.getEnabled () )
            {
                // skip configuration since it is disabled
                continue;
            }

            final ConnectionInformation ci = new ConnectionInformation ();
            ci.setUser ( configuration.getUser () );
            ci.setPassword ( configuration.getPassword () );
            ci.setDomain ( configuration.getDomain () );
            ci.setHost ( configuration.getHost () );
            ci.setClsid ( configuration.getClsid () );
            ci.setProgId ( configuration.getProgid () );

            final ConnectionSetup setup = new ConnectionSetup ( ci );

            if ( configuration.isSetIgnoreTimestampOnlyChange () )
            {
                setup.setIgnoreTimestampOnlyChange ( configuration.getIgnoreTimestampOnlyChange () );
            }

            if ( configuration.isSetReconnectDelay () )
            {
                setup.setReconnectDelay ( configuration.getReconnectDelay () );
            }
            else
            {
                setup.setReconnectDelay ( DEFAULT_RECONNECT_DELAY );
            }

            if ( configuration.isSetQualityErrorIfLessThen () )
            {
                setup.setQualityErrorIfLessThen ( Integer.valueOf ( configuration.getQualityErrorIfLessThen () ).shortValue () );
            }

            final String access = configuration.getAccess ();
            if ( access.equalsIgnoreCase ( "sync" ) )
            {
                setup.setAccessMethod ( AccessMethod.SYNC );
            }
            else if ( access.equalsIgnoreCase ( "async" ) )
            {
                setup.setAccessMethod ( AccessMethod.ASYNC20 );
            }
            else if ( access.equalsIgnoreCase ( "async20" ) )
            {
                setup.setAccessMethod ( AccessMethod.ASYNC20 );
            }

            setup.setFlatBrowser ( configuration.getFlatBrowser () );
            setup.setTreeBrowser ( configuration.getTreeBrowser () );

            setup.setRefreshTimeout ( configuration.getRefresh () );
            setup.setInitialConnect ( configuration.getInitialRefresh () );
            setup.setDeviceTag ( configuration.getAlias () );

            setup.setItemIdPrefix ( configuration.getItemIdPrefix () );

            if ( setup.getDeviceTag () == null )
            {
                setup.setDeviceTag ( setup.getConnectionInformation ().getHost () + ":" + setup.getConnectionInformation ().getClsOrProgId () );
            }

            final Collection<ItemSource> itemSources = new LinkedList<ItemSource> ();

            if ( configuration.getInitialItemList () != null )
            {
                itemSources.add ( new InMemoryAbstractItemSource ( "initialItems", new HashSet<String> ( configuration.getInitialItemList () ) ) );
            }
            if ( configuration.isSetInitialItemResource () )
            {
                itemSources.add ( new FileXMLItemSource ( "initialItemResource", configuration.getInitialItemResource () ) );
            }

            hive.addConnection ( setup, configuration.getConnected (), itemSources );
        }
    }
}
