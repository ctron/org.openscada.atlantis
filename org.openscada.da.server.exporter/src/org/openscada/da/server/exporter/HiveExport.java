/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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
        log.info ( String.format ( "Starting hive: %s", this.hive ) );

        this.hive.start ();

        for ( final Export export : this.exports )
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
        log.info ( String.format ( "Stopping hive: %s", this.hive ) );

        for ( final Export export : this.exports )
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

        this.hive.stop ();
    }

    public Export addExport ( final String endpointUri ) throws ConfigurationError
    {
        log.info ( String.format ( "Adding export: %s", endpointUri ) );

        final ConnectionInformation ci = ConnectionInformation.fromURI ( endpointUri );
        final Export export = findExport ( ci );

        if ( export != null )
        {
            this.exports.add ( export );
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
                return new NetExport ( this.hive, ci );
            }
            else if ( ci.getDriver ().equalsIgnoreCase ( "ice" ) )
            {
                log.debug ( "Create new 'ice' exporter" );
                // return new IceExport ( this.hive, ci );
                return null;
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
        return Collections.unmodifiableCollection ( this.exports );
    }
}
