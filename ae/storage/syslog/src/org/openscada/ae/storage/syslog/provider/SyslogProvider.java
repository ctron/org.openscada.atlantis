package org.openscada.ae.storage.syslog.provider;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openscada.ae.core.Event;
import org.openscada.ae.storage.syslog.DataStore;
import org.openscada.core.Variant;

public class SyslogProvider extends FileProviderBase
{
    private static Logger _log = Logger.getLogger ( SyslogProvider.class );
    
    private long _cnt = 0;
    private File _file = null;
    private String _sourceName = null;
    private String _severity = null;
    
    private Pattern _pattern = null;
    
    public SyslogProvider ( DataStore storage, File file, String sourceName, String severity )
    {
        super ( storage, file );
        _file = file;
        _pattern = Pattern.compile ( "(.*?[0-9]{2}:[0-9]{2}:[0-9]{2})\\s+(\\w+)\\s+(([\\w\\S]+):\\s*|)(.*)" );
        _sourceName = sourceName;
        _severity = severity;
    }

    @Override
    protected void handleLine ( String line )
    {
        Matcher matcher = _pattern.matcher ( line );
        if ( matcher.matches () )
        {
            Event event = new Event ( _file.getAbsolutePath () + "." + System.currentTimeMillis () + "." + _cnt );
            
            String timestamp = matcher.group ( 1 );
            String host = matcher.group ( 2 );
            String app = matcher.group ( 4 );
            String message = matcher.group ( 5 );
            
            event.setTimestamp ( parseDate ( timestamp ) );
            event.getAttributes ().put ( "raw_timestamp", new Variant ( timestamp ) );
            event.getAttributes ().put ( "host", new Variant ( host ) );
            event.getAttributes ().put ( "application", new Variant ( app ) );
            event.getAttributes ().put ( "message", new Variant ( message ) );
            event.getAttributes ().put ( "raw", new Variant ( matcher.group ( 0 ) ) );
            event.getAttributes ().put ( "source", new Variant ( _sourceName ) );
            event.getAttributes ().put ( "severity", new Variant ( _severity ) );
            
            submitEvent ( event );
            
            _cnt++;
        }
        else
            _log.debug ( "did not match: " + line );
    }
    
    protected Calendar parseDate ( String date )
    {
        try
        {
            Calendar timestamp = Calendar.getInstance ();
            DateFormat df = new SimpleDateFormat ( "MMM d HH:mm:ss" );
            timestamp.setTime ( df.parse ( date ) );
            timestamp.set ( Calendar.YEAR, Calendar.getInstance ().get ( Calendar.YEAR  ) );
            return timestamp;
        }
        catch ( Exception e )
        {
            _log.info ( "Failed to parse date: " + date, e );
        }
        return Calendar.getInstance ();
    }

}
