package org.openscada.ae.submitter.net;

import java.util.Properties;

import org.openscada.ae.core.Event;
import org.openscada.ae.core.Submission;
import org.openscada.core.client.net.ConnectionInfo;

public class Submitter implements Submission
{
    
    private ConnectionInfo _connectionInformation = null;
    private Connection _connection = null;
    
    public Submitter ()
    {
        super ();
        
        _connectionInformation = new ConnectionInfo ();
        _connectionInformation.setAutoReconnect ( false );
        _connectionInformation.setHostName ( System.getProperty ( "openscada.ae.submitter.net.hostname" ) );
        _connectionInformation.setPort ( Integer.getInteger ( "openscada.ae.submitter.net.port", 1302 ) );
    }
    
    public Submitter ( ConnectionInfo connectionInfo )
    {
        super ();
        
        _connectionInformation = connectionInfo;
    }
    
    synchronized protected Connection getConnection () throws Throwable
    {
        if ( _connection == null )
        {
            _connection = new Connection ( _connectionInformation );
            _connection.connect ();
            _connection.waitForConnection ();
        }
        return _connection;
    }
    
    public void submitEvent ( Properties properties, Event event ) throws Throwable
    {
        getConnection ().submitEvent ( properties, event );
    }
    
}
