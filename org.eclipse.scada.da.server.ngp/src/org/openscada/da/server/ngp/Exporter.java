/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.ngp;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.scada.core.ConnectionInformation;
import org.eclipse.scada.protocol.ngp.common.ProtocolConfigurationFactory;
import org.eclipse.scada.utils.lifecycle.LifecycleAware;
import org.openscada.core.server.exporter.ExporterInformation;
import org.openscada.da.common.ngp.ProtocolConfigurationFactoryImpl;
import org.openscada.da.core.server.Hive;

public class Exporter implements LifecycleAware
{
    private Server server;

    private final ProtocolConfigurationFactory protocolConfigurationFactory;

    private Collection<InetSocketAddress> addresses = new LinkedList<InetSocketAddress> ();

    private final Hive service;

    private Set<InetSocketAddress> startedAddresses;

    public Exporter ( final Hive service, final ProtocolConfigurationFactory protocolConfigurationFactory, final Collection<InetSocketAddress> addresses )
    {
        this.service = service;
        this.protocolConfigurationFactory = protocolConfigurationFactory;
        this.addresses = addresses;
    }

    public Exporter ( final Hive service, final ConnectionInformation connectionInformation ) throws Exception
    {
        this ( service, new ProtocolConfigurationFactoryImpl ( connectionInformation ), Collections.singletonList ( new InetSocketAddress ( connectionInformation.getTarget (), connectionInformation.getSecondaryTarget () ) ) );
    }

    public Class<? extends Hive> getServiceClass ()
    {
        return this.service.getClass ();
    }

    private Set<InetSocketAddress> createServer () throws Exception
    {
        this.server = new Server ( this.addresses, this.protocolConfigurationFactory, this.service );
        return this.server.start ();
    }

    @Override
    public void start () throws Exception
    {
        if ( this.startedAddresses == null )
        {
            this.startedAddresses = createServer ();
        }
    }

    public Set<InetSocketAddress> getStartedAddresses ()
    {
        return this.startedAddresses;
    }

    /**
     * @since 1.1
     */
    public Set<ConnectionInformation> getStartedConnectionInformations ()
    {
        return convert ( "da", getStartedAddresses () );
    }

    public Set<ExporterInformation> getExporterInformation ()
    {
        final Set<ExporterInformation> result = new HashSet<ExporterInformation> ();

        for ( final ConnectionInformation ci : getStartedConnectionInformations () )
        {
            result.add ( new ExporterInformation ( ci, null ) );
        }

        return result;
    }

    Set<ConnectionInformation> convert ( final String interfaceName, final Set<InetSocketAddress> startedAddresses )
    {
        final HashSet<ConnectionInformation> result = new HashSet<ConnectionInformation> ();

        for ( final InetSocketAddress address : startedAddresses )
        {
            final String target = address.getAddress ().getHostAddress ().replace ( "%", "%25" ); // for IPv6

            final ConnectionInformation ci;
            if ( target.indexOf ( ':' ) >= 0 )
            {
                ci = ConnectionInformation.fromURI ( String.format ( "%s:ngp://[%s]:%s", interfaceName, target, address.getPort () ) );
            }
            else
            {
                ci = ConnectionInformation.fromURI ( String.format ( "%s:ngp://%s:%s", interfaceName, target, address.getPort () ) );
            }
            if ( ci != null )
            {
                result.add ( ci );
            }
        }

        return result;
    }

    @Override
    public void stop () throws Exception
    {
        destroyServer ();
    }

    private void destroyServer ()
    {
        if ( this.server != null )
        {
            this.server.dispose ();
            this.server = null;
            this.startedAddresses = null;
        }
    }
}
