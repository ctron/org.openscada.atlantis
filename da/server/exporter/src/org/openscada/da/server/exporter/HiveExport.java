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

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.common.configuration.ConfigurationError;

public class HiveExport
{
    private static Logger _log = Logger.getLogger ( HiveExport.class );

    private Hive _hive = null;

    private final List<Export> _exports = new LinkedList<Export> ();

    public HiveExport ( final Hive hive )
    {
        super ();
        this._hive = hive;
    }

    public synchronized void start ()
    {
        _log.info ( String.format ( "Starting hive: %s", this._hive ) );

        for ( final Export export : this._exports )
        {
            try
            {
                export.start ();
            }
            catch ( final Exception e )
            {
                _log.error ( "Failed to start export", e );
            }
        }
    }

    public synchronized void stop ()
    {
        _log.info ( String.format ( "Stopping hive: %s", this._hive ) );

        for ( final Export export : this._exports )
        {
            try
            {
                export.stop ();
            }
            catch ( final Exception e )
            {
                _log.error ( "Failed to stop export", e );
            }
        }
    }

    public Export addExport ( final String endpointUri ) throws ConfigurationError
    {
        final ConnectionInformation ci = ConnectionInformation.fromURI ( endpointUri );
        final Export export = findExport ( ci );

        if ( export != null )
        {
            this._exports.add ( export );
        }

        return export;
    }

    protected Export findExport ( final ConnectionInformation ci ) throws ConfigurationError
    {
        if ( !ci.getInterface ().equalsIgnoreCase ( "da" ) )
        {
            throw new ConfigurationError ( String.format ( "Interface must be 'da' but is '%s'", ci.getInterface () ) );
        }

        if ( ci.getDriver ().equalsIgnoreCase ( "net" ) || ci.getDriver ().equalsIgnoreCase ( "gmpp" ) )
        {
            return new NetExport ( this._hive, ci );
        }
        else if ( ci.getDriver ().equalsIgnoreCase ( "ice" ) )
        {
            return new IceExport ( this._hive, ci );
        }
        else
        {
            throw new ConfigurationError ( String.format ( "Driver '%s' is unknown", ci.getDriver () ) );
        }
    }
}
