/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.server.exporter;

import java.io.IOException;
import java.util.Hashtable;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.net.Exporter;

public class NetExport implements Export
{
    private static Logger logger = Logger.getLogger ( NetExport.class );

    private Hive hive = null;

    private Exporter exporter = null;

    private final ConnectionInformation connectionInformation;

    private final Controller controller;

    public NetExport ( final Controller controller, final Hive hive, final ConnectionInformation connectionInformation ) throws Exception
    {
        super ();
        this.hive = hive;

        this.connectionInformation = connectionInformation;
        this.controller = controller;

        logger.debug ( "Instatiate exporter class" );

        this.exporter = new Exporter ( this.hive, this.connectionInformation );
    }

    public synchronized void start () throws Exception
    {
        if ( this.exporter == null )
        {
            return;
        }

        logger.info ( String.format ( "Starting exporter (%s) on port %s", this.hive, this.connectionInformation ) );

        this.exporter.start ();

        registerBonjour ();
    }

    private void registerBonjour () throws IOException
    {
        final JmDNS bonjour = this.controller.getBonjour ();
        if ( bonjour != null )
        {
            final String type = String.format ( "_openscada_%s_%s._tcp.local", this.connectionInformation.getInterface (), this.connectionInformation.getDriver () );
            final Hashtable<Object, Object> props = new Hashtable<Object, Object> ();
            props.putAll ( this.connectionInformation.getProperties () );

            String name = this.connectionInformation.toString ();
            name = name.replace ( '.', '_' );

            final ServiceInfo info = new ServiceInfo ( type, name, this.connectionInformation.getSecondaryTarget (), 0, 0, props );

            logger.info ( "Exporting using zeroconf: " + info );

            bonjour.registerService ( info );
        }
    }

    public void stop () throws Exception
    {
        this.exporter.stop ();
        unregisterBonjour ();
    }

    private void unregisterBonjour ()
    {
        final JmDNS bonjour = this.controller.getBonjour ();
        if ( bonjour != null )
        {
            final String type = String.format ( "_openscada_%s_%s", this.connectionInformation.getInterface (), this.connectionInformation.getDriver () );
            final Hashtable<Object, Object> props = new Hashtable<Object, Object> ();
            props.putAll ( this.connectionInformation.getProperties () );
            final ServiceInfo info = new ServiceInfo ( type, this.connectionInformation.toString (), this.connectionInformation.getSecondaryTarget (), 0, 0, props );
            bonjour.unregisterService ( info );
        }
    }

}
