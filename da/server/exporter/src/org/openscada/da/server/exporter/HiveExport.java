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
    private List<Export> _exports = new LinkedList<Export> ();
    
    public HiveExport ( String hiveClass ) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        super ();
        _hive = createInstance ( hiveClass );
    }
    
    protected static Hive createInstance ( String hiveClassName ) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Class hiveClass = Class.forName ( hiveClassName );
        return (Hive)hiveClass.newInstance ();
    }
    
    public synchronized void start ()
    {
        _log.info ( String.format ( "Starting hive: %s", _hive ) );
        
        for ( Export export : _exports )
        {
            try
            {
                export.start ();
            }
            catch ( Exception e )
            {
                _log.error ( "Failed to start export", e );
            }
        }
    }
    
    public synchronized void stop ()
    {
        _log.info ( String.format ( "Stopping hive: %s", _hive ) );
        
        for ( Export export : _exports )
        {
            try
            {
                export.stop ();
            }
            catch ( Exception e )
            {
                _log.error ( "Failed to stop export", e );
            }
        }
    }

    public Export addExport ( ExportType exportType ) throws ConfigurationError
    {
        ConnectionInformation ci = ConnectionInformation.fromURI ( exportType.getUri () );
        Export export = findExport ( ci );
        
        if ( export != null )
        {
            _exports.add ( export );
        }
        
        return export;
    }
    
    protected Export findExport ( ConnectionInformation ci ) throws ConfigurationError
    {
        if ( !ci.getInterface ().equalsIgnoreCase ( "da" ) )
        {
            throw new ConfigurationError ( String.format ( "Interface must be 'da' but is '%s'", ci.getInterface () ) );
        }
        
        if ( ci.getDriver ().equalsIgnoreCase ( "net" ) || ci.getDriver ().equalsIgnoreCase ( "gmpp" ) )
        {
            return new NetExport ( _hive, ci );
        }
        else if ( ci.getDriver ().equalsIgnoreCase ( "ice" ) )
        {
            return new IceExport ( _hive, ci );
        }
        else
        {
            throw new ConfigurationError ( String.format ( "Driver '%s' is unknown", ci.getDriver () ) );
        }
    }
}
