/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.net.test;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.openscada.net.line.LineBasedConnection;
import org.openscada.net.line.LineHandler;

public class LineHandlerTestImpl implements LineHandler
{
    private static Logger log = Logger.getLogger ( LineHandlerTestImpl.class );

    private LineBasedConnection connection = null;

    private boolean echo = false;

    public LineHandlerTestImpl ( final boolean echo )
    {
        this.echo = echo;
    }

    public void handleLine ( final String line )
    {
        log.info ( "New line: '" + line + "'" );

        if ( this.echo )
        {
            this.connection.sendLine ( "000 ECHO " + line );
        }

        try
        {
            final StringTokenizer tok = new StringTokenizer ( line );
            final String cmd = tok.nextToken ().toUpperCase ();
            if ( cmd.equals ( "QUIT" ) || cmd.equals ( "CLOSE" ) || cmd.equals ( "EXIT" ) )
            {
                this.connection.sendLine ( "000 Bye" );
                this.connection.close ();
            }
        }
        catch ( final Exception e )
        {
            this.connection.sendLine ( "999 Command failed: " + e.getMessage () );
        }
        log.debug ( "Line handler complete" );

    }

    @Override
    protected void finalize () throws Throwable
    {
        log.info ( "Finalized" );
        this.connection.close ();
        super.finalize ();
    }

    public void closed ()
    {
        log.info ( "Closed" );
        this.connection = null;
    }

    public void connected ()
    {
        log.info ( "Connected" );
        this.connection.sendLine ( "000 Welcome" );
    }

    public void connectionFailed ( final Throwable throwable )
    {
        log.info ( "Connection failed", throwable );
    }

    public void setConnection ( final LineBasedConnection connection )
    {
        this.connection = connection;
        if ( this.connection != null )
        {
            this.connection.getConnection ().setTimeout ( 10 * 1000 );
        }
    }

}
