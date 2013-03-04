/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.hd.common.ngp;

import java.util.Arrays;
import java.util.Map;

import org.openscada.core.ConnectionInformation;
import org.openscada.hd.protocol.ngp.codec.ProtocolFactory;
import org.openscada.protocol.ngp.common.DefaultProtocolConfigurationFactory;
import org.openscada.protocol.ngp.common.ProtocolConfiguration;

public class ProtocolConfigurationFactoryImpl extends DefaultProtocolConfigurationFactory
{
    public ProtocolConfigurationFactoryImpl ( final ConnectionInformation connectionInformation )
    {
        super ( connectionInformation );
    }

    @Override
    protected void customizeConfiguration ( final ProtocolConfiguration configuration, final boolean clientMode )
    {
        // check if we prefer the binary protocol
        if ( preferJava () )
        {
            addJavaProtocol ( ProtocolFactory.VERSION, configuration, ProtocolConfigurationFactoryImpl.class.getClassLoader () );
            addProtocol ( configuration, ProtocolFactory.createProtocolDescriptor () );
        }
        else
        {
            addProtocol ( configuration, ProtocolFactory.createProtocolDescriptor () );
            addJavaProtocol ( ProtocolFactory.VERSION, configuration, ProtocolConfigurationFactoryImpl.class.getClassLoader () );
        }

        if ( preferJava () )
        {
            configuration.setPreferredProtocols ( Arrays.asList ( "java/" + ProtocolFactory.VERSION ) );
        }
    }

    private boolean preferJava ()
    {
        final Map<String, String> props = this.connectionInformation.getProperties ();
        final String useJava = props.get ( "useJava" );
        return Boolean.parseBoolean ( useJava );
    }
}
