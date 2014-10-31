/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.opc.configuration;

import org.openscada.da.opc.configuration.ConfigurationType;
import org.openscada.da.opc.configuration.RootType;
import org.openscada.da.server.opc.Hive;
import org.openscada.da.server.opc.connection.data.AccessMethod;
import org.openscada.da.server.opc.connection.data.ConnectionSetup;
import org.openscada.opc.lib.common.ConnectionInformation;

public class XMLConfigurator
{

    private static final int DEFAULT_RECONNECT_DELAY = 5000;

    private final RootType root;

    public XMLConfigurator ( final RootType root )
    {
        this.root = root;
    }

    public void configure ( final Hive hive )
    {
        for ( final ConfigurationType configuration : this.root.getConnections ().getConfiguration () )
        {
            if ( !configuration.isEnabled () )
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
                setup.setIgnoreTimestampOnlyChange ( configuration.isIgnoreTimestampOnlyChange () );
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

            setup.setFlatBrowser ( configuration.isFlatBrowser () );
            setup.setTreeBrowser ( configuration.isTreeBrowser () );

            setup.setUpdateRate ( configuration.getRefresh () );
            setup.setInitialConnect ( configuration.isInitialRefresh () );
            setup.setDeviceTag ( configuration.getAlias () );

            setup.setItemIdPrefix ( configuration.getItemIdPrefix () );

            if ( setup.getDeviceTag () == null )
            {
                setup.setDeviceTag ( setup.getConnectionInformation ().getHost () + ":" + setup.getConnectionInformation ().getClsOrProgId () );
            }

            hive.addConnection ( setup, configuration.isConnected () );
        }
    }
}
