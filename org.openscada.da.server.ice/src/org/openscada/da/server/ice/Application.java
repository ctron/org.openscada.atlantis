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

package org.openscada.da.server.ice;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;

public class Application extends Ice.Application
{
    private static Logger log = Logger.getLogger ( Application.class );

    @SuppressWarnings ( "unused" )
    private Exporter e;

    @Override
    public int run ( final String[] args )
    {
        try
        {
            log.debug ( String.format ( "Try to export hive '%s'", args[0] ) );

            final ConnectionInformation ci;

            if ( args.length > 1 )
            {
                ci = ConnectionInformation.fromURI ( args[1] );
            }
            else
            {
                ci = ConnectionInformation.fromURI ( "da:ice://Hive" );
            }

            final Class<?> hiveClass = Class.forName ( args[0] );
            final Hive hive = (Hive)hiveClass.newInstance ();
            hive.start ();
            this.e = new Exporter ( hive, communicator (), ci );

            communicator ().waitForShutdown ();

            hive.stop ();

            return 0;
        }
        catch ( final Exception e )
        {
            log.error ( "Failed to start exporter", e );
            return 1;
        }
    }

    public static void main ( final String[] args )
    {
        new Application ().main ( "Hive", args, System.getProperty ( "openscada.ice.config" ) );
    }

}
