package org.openscada.ae.storage.syslog.provider;

import java.io.File;
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
    
    private Calendar _lastTimestamp = null;
    private long _cnt = 0;
    
    private File _file = null;
    private String _sourceName = null;
    private String _severity = null;
    
    private Pattern _pattern = null;
    private DateParser _dateParser = new SyslogDateParser ();
    
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
            String timestamp = matcher.group ( 1 );
            String host = matcher.group ( 2 );
            String app = matcher.group ( 4 );
            String message = matcher.group ( 5 );
         
            Calendar datetime = _dateParser.parseDate ( timestamp );
            
            Event event = new Event ( getNextEventId ( datetime, host ) );
            
            event.setTimestamp ( _dateParser.parseDate ( timestamp ) );
            event.getAttributes ().put ( "raw_timestamp", new Variant ( timestamp ) );
            event.getAttributes ().put ( "host", new Variant ( host ) );
            event.getAttributes ().put ( "application", new Variant ( app ) );
            event.getAttributes ().put ( "message", new Variant ( message ) );
            event.getAttributes ().put ( "raw", new Variant ( matcher.group ( 0 ) ) );
            event.getAttributes ().put ( "source", new Variant ( _sourceName ) );
            event.getAttributes ().put ( "severity", new Variant ( _severity ) );
            
            submitEvent ( event );
        }
        else
            _log.debug ( "did not match: " + line );
    }
    
    protected String getNextEventId ( Calendar timestamp, String hostname )
    {
        if ( _lastTimestamp == null )
        {
            _cnt = 0;
            _lastTimestamp = timestamp;
        }
        else if ( _lastTimestamp.equals ( timestamp ) )
        {
            _cnt++;
        }
        else
        {
            _lastTimestamp = timestamp;
            _cnt = 0;
        }
        return hostname + "." + _file.getAbsolutePath () + "." + _lastTimestamp.getTimeInMillis () + "." + _cnt;
    }
}
