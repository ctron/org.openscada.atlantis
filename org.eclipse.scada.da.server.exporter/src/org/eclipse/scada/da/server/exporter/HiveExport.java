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

package org.eclipse.scada.da.server.exporter;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.scada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiveExport
{

    private final static Logger logger = LoggerFactory.getLogger ( HiveExport.class );

    private final Hive hive;

    private final Collection<Export> exports = new CopyOnWriteArrayList<Export> ();

    public HiveExport ( final Hive hive )
    {
        super ();
        this.hive = hive;
    }

    public synchronized void start () throws Exception
    {
        logger.info ( "Starting hive: {}", this.hive );

        this.hive.start ();

        for ( final Export export : this.exports )
        {
            try
            {
                export.start ();
            }
            catch ( final Exception e )
            {
                logger.error ( "Failed to start export", e );
            }
        }
    }

    public synchronized void stop () throws Exception
    {
        logger.info ( "Stopping hive: {}", this.hive );

        for ( final Export export : this.exports )
        {
            try
            {
                export.stop ();
            }
            catch ( final Exception e )
            {
                logger.error ( "Failed to stop export", e );
            }
        }

        this.hive.stop ();
    }

    public Export addExport ( final String endpointUri ) throws ConfigurationException
    {
        logger.info ( "Adding export: {}", endpointUri );

        final ConnectionInformation ci = ConnectionInformation.fromURI ( endpointUri );
        final Export export = findExport ( ci );

        if ( export != null )
        {
            this.exports.add ( export );
        }
        else
        {
            logger.info ( "No exporter found for endpoint: {}", endpointUri );
            throw new ConfigurationException ( String.format ( "No exporter found for endpoint: %s", endpointUri ) );
        }

        return export;
    }

    protected Export findExport ( final ConnectionInformation ci ) throws ConfigurationException
    {
        logger.info ( "Requested export to: {}", ci );

        if ( !ci.getInterface ().equalsIgnoreCase ( "da" ) )
        {
            throw new ConfigurationException ( String.format ( "Interface must be 'da' but is '%s'", ci.getInterface () ) );
        }

        try
        {
            if ( ci.getDriver ().equalsIgnoreCase ( "net" ) || ci.getDriver ().equalsIgnoreCase ( "gmpp" ) )
            {
                logger.debug ( "Create new 'net' exporter" );
                return new NetExport ( this.hive, ci );
            }
            else if ( ci.getDriver ().equalsIgnoreCase ( "ngp" ) )
            {
                logger.debug ( "Create new 'ngp' exporter" );
                return new NgpExport ( this.hive, ci );
            }
            else
            {
                throw new ConfigurationException ( String.format ( "Driver '%s' is unknown", ci.getDriver () ) );
            }
        }
        catch ( final Throwable e )
        {
            throw new ConfigurationException ( "Failed to configure exporter", e );
        }
    }

    public Collection<Export> getExports ()
    {
        return Collections.unmodifiableCollection ( this.exports );
    }
}
