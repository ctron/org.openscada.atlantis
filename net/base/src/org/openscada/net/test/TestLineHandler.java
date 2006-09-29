/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

public class TestLineHandler implements LineHandler
{
    private static Logger _log = Logger.getLogger ( TestLineHandler.class );

    private LineBasedConnection _connection = null;

    private boolean _echo = false;

    public TestLineHandler ( boolean echo )
    {
        _echo = echo;
    }

    public void handleLine ( String line )
    {
        _log.info ( "New line: '" + line + "'" );

        if ( _echo )
            _connection.sendLine ( "000 ECHO " + line );

        try
        {
            StringTokenizer tok = new StringTokenizer ( line );
            String cmd = tok.nextToken ().toUpperCase ();
            if ( cmd.equals ( "QUIT" ) || cmd.equals ( "CLOSE" ) || cmd.equals ( "EXIT" ) )
            {
                _connection.sendLine ( "000 Bye" );
                _connection.close ();
            }
        }
        catch ( Exception e )
        {
            _connection.sendLine ( "999 Command failed: " + e.getMessage () );
        }
        _log.debug ( "Line handler complete" );

    }

    @Override
    protected void finalize () throws Throwable
    {
        _log.info ( "Finalized" );
        _connection.close ();
        _connection = null;
        super.finalize ();
    }

    public void closed ()
    {
        _log.info ( "Closed" );
        _connection = null;
    }

    public void connected ()
    {
        _log.info ( "Connected" );
        _connection.sendLine ( "000 Welcome" );
    }

    public void connectionFailed ( Throwable throwable )
    {
        _log.info ( "Connection failed", throwable );
    }

    public void setConnection ( LineBasedConnection connection )
    {
        _connection = connection;
        if ( _connection != null )
            _connection.getConnection ().setTimeout ( 10 * 1000 );
    }

}
