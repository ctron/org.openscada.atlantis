/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.client.samples;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.ConnectWaitController;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.da.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleBase
{

    private final static Logger logger = LoggerFactory.getLogger ( SampleBase.class );

    protected String uri;

    protected Connection connection;

    public SampleBase ( final String uri, final String className ) throws Exception
    {
        super ();

        this.uri = uri;

        // If we got a class name load it
        if ( className != null )
        {
            Class.forName ( className );
        }

        if ( this.uri == null )
        {
            this.uri = "da:net://localhost:1202";
        }

        final ConnectionInformation ci = ConnectionInformation.fromURI ( this.uri );

        this.connection = (Connection)ConnectionFactory.create ( ci );
        if ( this.connection == null )
        {
            throw new Exception ( "Unable to find a connection driver for specified URI" );
        }

        this.connection.addConnectionStateListener ( new ConnectionStateListener () {

            @Override
            public void stateChange ( final org.openscada.core.client.Connection connection, final ConnectionState state, final Throwable error )
            {
                logger.info ( "Connection state changed: " + state, error );
            }
        } );

    }

    public void connect () throws Exception
    {
        // trigger the connection
        try
        {
            // wait until the connection is established. If it already is the call
            // will return immediately.
            // If the connect attempt fails an exception is thrown.
            new ConnectWaitController ( this.connection ).connect ();
        }
        catch ( final Throwable e )
        {
            // we were unlucky
            throw new Exception ( "Unable to create connection", e );
        }
    }

    public void dispose ()
    {
        this.connection.dispose ();
        this.connection = null;
        System.gc ();
    }

    public static void sleep ( final long millis )
    {
        try
        {
            Thread.sleep ( millis );
        }
        catch ( final Throwable e )
        {
            e.printStackTrace ();
        }
    }

}