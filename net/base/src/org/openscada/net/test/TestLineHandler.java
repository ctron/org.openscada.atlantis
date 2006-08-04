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
            _connection.getConnection ().setTimeOut ( 10*1000 );
    }

}
