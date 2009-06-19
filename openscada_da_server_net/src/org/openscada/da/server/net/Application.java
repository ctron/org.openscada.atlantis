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

package org.openscada.da.server.net;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;

/**
 * Application to export a hive using the OpenSCADA NET protocol
 * @author Jens Reimann
 *
 */
public class Application
{
    private static Logger logger = Logger.getLogger ( Application.class );

    public static void main ( final String[] args )
    {
        try
        {
            // check if we have a class name
            if ( args.length < 1 )
            {
                System.err.println ( "syntax: Application <hiveClassName>" );
                return;
            }
            ConnectionInformation ci = null;
            if ( args.length >= 2 )
            {
                ci = ConnectionInformation.fromURI ( args[1] );
            }
            if ( ci == null )
            {
                ci = ConnectionInformation.fromURI ( "da:net://0.0.0.0:" + System.getProperty ( "openscada.da.net.server.port", "1202" ) );
            }

            // create exporter
            final Class<?> hiveClass = Class.forName ( args[0] );
            final Hive hive = (Hive)hiveClass.newInstance ();
            final Exporter exporter = new Exporter ( hive, ci );
            hive.start ();
            exporter.start ();

            // run the lizzard
            logger.info ( "Running exporter (hive class: " + exporter.getHiveClass ().getCanonicalName () + ")..." );
        }
        catch ( final Throwable e )
        {
            // ops
            logger.fatal ( "Error in OpenSCADA DA[NET] Server", e );
            System.exit ( 1 );
        }
    }
}
