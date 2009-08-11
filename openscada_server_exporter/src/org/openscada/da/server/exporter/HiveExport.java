/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.common.configuration.ConfigurationError;

public class HiveExport
{
    private static Logger log = Logger.getLogger ( HiveExport.class );

    private Hive hive = null;

    private final Collection<Export> exports = new CopyOnWriteArrayList<Export> ();

    public HiveExport ( final Hive hive )
    {
        super ();
        this.hive = hive;
    }

    public synchronized void start () throws Exception
    {
        log.info ( String.format ( "Starting hive: %s", hive ) );

        hive.start ();

        for ( final Export export : exports )
        {
            try
            {
                export.start ();
            }
            catch ( final Exception e )
            {
                log.error ( "Failed to start export", e );
            }
        }
    }

    public synchronized void stop () throws Exception
    {
        log.info ( String.format ( "Stopping hive: %s", hive ) );

        for ( final Export export : exports )
        {
            try
            {
                export.stop ();
            }
            catch ( final Exception e )
            {
                log.error ( "Failed to stop export", e );
            }
        }

        hive.stop ();
    }

    public Export addExport ( final String endpointUri ) throws ConfigurationError
    {
        log.info ( String.format ( "Adding export: %s", endpointUri ) );

        final ConnectionInformation ci = ConnectionInformation.fromURI ( endpointUri );
        final Export export = findExport ( ci );

        if ( export != null )
        {
            exports.add ( export );
        }
        else
        {
            log.info ( String.format ( "No exporter found for endpoint: %s", endpointUri ) );
            throw new ConfigurationError ( String.format ( "No exporter found for endpoint: %s", endpointUri ) );
        }

        return export;
    }

    protected Export findExport ( final ConnectionInformation ci ) throws ConfigurationError
    {
        log.info ( String.format ( "Requested export to: %s", ci ) );

        if ( !ci.getInterface ().equalsIgnoreCase ( "da" ) )
        {
            throw new ConfigurationError ( String.format ( "Interface must be 'da' but is '%s'", ci.getInterface () ) );
        }

        try
        {
            if ( ci.getDriver ().equalsIgnoreCase ( "net" ) || ci.getDriver ().equalsIgnoreCase ( "gmpp" ) )
            {
                log.debug ( "Create new 'net' exporter" );
                return new NetExport ( hive, ci );
            }
            else if ( ci.getDriver ().equalsIgnoreCase ( "ice" ) )
            {
                log.debug ( "Create new 'ice' exporter" );
                return new IceExport ( hive, ci );
            }
            else
            {
                throw new ConfigurationError ( String.format ( "Driver '%s' is unknown", ci.getDriver () ) );
            }
        }
        catch ( final Throwable e )
        {
            throw new ConfigurationError ( "Failed to configure exporter", e );
        }
    }

    public Collection<Export> getExports ()
    {
        return Collections.unmodifiableCollection ( exports );
    }
}
