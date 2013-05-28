/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.client.samples;

import java.util.Calendar;
import java.util.Random;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.Variant;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.da.client.Connection;
import org.openscada.da.client.WriteOperationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriterTest1
{
    private final static Logger logger = LoggerFactory.getLogger ( WriterTest1.class );

    public static void main ( final String[] args ) throws ClassNotFoundException
    {
        final String className = "org.openscada.da.client.net.Connection";
        final String uri = "da:net://127.0.0.1:1202";
        final String itemName = "172.16.148.128:F8582CF2-88FB-11D0-B850-00C0F0104305.Bucket Brigade.Int4";
        // final String itemName = "memory";

        if ( className != null )
        {
            Class.forName ( className );
        }

        final ConnectionInformation ci = ConnectionInformation.fromURI ( uri );

        final Connection connection = (Connection)ConnectionFactory.create ( ci );
        if ( connection == null )
        {
            throw new RuntimeException ( "Unable to find a connection driver for specified URI" );
        }

        connection.connect ();

        new Thread ( new Runnable () {

            @Override
            public void run ()
            {
                while ( true )
                {
                    try
                    {
                        Thread.sleep ( 1000 );
                    }
                    catch ( final InterruptedException e )
                    {
                        return;
                    }
                    doWrite ( connection, itemName );
                }
            }
        } ).start ();
    }

    private static Random random = new Random ();

    public static void doWrite ( final Connection connection, final String itemName )
    {
        final Variant value = Variant.valueOf ( random.nextInt () );
        logger.info ( "Start write: " + value );
        final Calendar c = Calendar.getInstance ();
        final Object lock = new Object ();
        synchronized ( lock )
        {
            connection.write ( itemName, value, null, new WriteOperationCallback () {

                @Override
                public void complete ()
                {
                    logger.info ( String.format ( "Wrote: %s, Started: %tc", value, c ) );
                    synchronized ( lock )
                    {
                        lock.notify ();
                    }
                }

                @Override
                public void error ( final Throwable e )
                {
                    logger.info ( "Error", e );
                    synchronized ( lock )
                    {
                        lock.notify ();
                    }
                }

                @Override
                public void failed ( final String error )
                {
                    logger.info ( "Failed: " + error );
                    // async call since it might called inline
                    new Thread ( new Runnable () {

                        @Override
                        public void run ()
                        {
                            synchronized ( lock )
                            {
                                lock.notify ();
                            }
                        }
                    } ).start ();

                }
            } );

            /*
            try
            {
                lock.wait ();
            }
            catch ( InterruptedException e1 )
            {
            }
            */
        }
    }
}
