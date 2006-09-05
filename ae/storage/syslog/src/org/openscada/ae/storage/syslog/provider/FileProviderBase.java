package org.openscada.ae.storage.syslog.provider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.openscada.ae.core.Event;
import org.openscada.ae.storage.syslog.DataStore;

public abstract class FileProviderBase implements Runnable
{
    private static Logger _log = Logger.getLogger ( FileProviderBase.class );
    
    private DataStore _storage = null;
    private File _file = null;
    private Thread _thread = new Thread ( this );
    
    public FileProviderBase ( DataStore storage, File file )
    {
        super ();
        _storage = storage;
        _file = file;
        
        _thread.setDaemon ( true );
        _thread.start ();
    }
    
    public void run ()
    {
        while ( true )
        {
            try
            {
                runOnce ();
            }
            catch ( Exception e )
            {
                _log.debug ( "read failed", e );
            }
            try
            {
                Thread.sleep ( 1 * 1000 );
            }
            catch ( InterruptedException e )
            {
                _log.debug ( "sleep failed", e );
            }
        }
    }
    
    public void runOnce () throws Exception
    {
        FileReader fileReader;

        fileReader = new FileReader ( _file );
        BufferedReader bufferedReader = new BufferedReader ( fileReader );

        while ( true )
        {
            String line; 
            while ( ( line = bufferedReader.readLine () ) != null )
            {
                handleLine ( line );
            }
            Thread.sleep ( 100 );
        }
    }
    
    protected abstract void handleLine ( String line );
    
    protected void submitEvent ( Event event )
    {
        if ( _storage != null )
            _storage.submitEvent ( event );
    }
}
