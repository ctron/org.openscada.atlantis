package org.openscada.ae.storage.syslog.provider;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openscada.ae.core.Event;
import org.openscada.ae.storage.syslog.DataStore;
import org.openscada.core.Variant;

public class SyslogParser
{
    private static Logger _log = Logger.getLogger ( SyslogParser.class );
    
    private DataStore _store = null;
    private String _sourceName = null; 
    private String _defaultSeverity = null;

    private Pattern _pattern = null;
    private DateParser _dateParser = new SyslogDateParser ();

    private Calendar _lastTimestamp = null;
    private long _cnt = 0;
    
    public SyslogParser ( DataStore store, String sourceName, String severity )
    {
        super ();
        _store = store;
        _sourceName = sourceName;
        _defaultSeverity = severity;
        
        _pattern = Pattern.compile ( "(\\<([0-9]+)\\>|)(.*?[0-9]{2}:[0-9]{2}:[0-9]{2})\\s+(\\w+)\\s+(([\\w\\S]+):\\s*|)(.*)" );
    }
    
    public void handleLine ( String line )
    {
        Matcher matcher = _pattern.matcher ( line );
        if ( matcher.matches () )
        {   
            String severity = _defaultSeverity;
            String facility = null;
            String syslogPriority = null;
            
            try
            {
                int messageCode = Integer.parseInt ( matcher.group ( 2 ) );
                severity = SyslogHelper.getPriorityNameConverted ( SyslogHelper.getPriority ( messageCode ) );
                facility = SyslogHelper.getFacilityName ( SyslogHelper.getFacility ( messageCode ) );
                syslogPriority = SyslogHelper.getPriorityName ( SyslogHelper.getPriority ( messageCode ) );
            }
            catch ( Exception e )
            {}
            
            String timestamp = matcher.group ( 3 );
            String host = matcher.group ( 4 );
            String app = matcher.group ( 6 );
            String message = matcher.group ( 7 );
         
            Calendar datetime = _dateParser.parseDate ( timestamp );
            
            Event event = new Event ( getNextEventId ( datetime, _sourceName, host ) );
            
            event.setTimestamp ( _dateParser.parseDate ( timestamp ) );
            event.getAttributes ().put ( "syslog.raw.timestamp", new Variant ( timestamp ) );
            event.getAttributes ().put ( "host", new Variant ( host ) );
            event.getAttributes ().put ( "application", new Variant ( app ) );
            event.getAttributes ().put ( "message", new Variant ( message ) );
            event.getAttributes ().put ( "raw", new Variant ( matcher.group ( 0 ) ) );
            event.getAttributes ().put ( "source", new Variant ( _sourceName ) );
            event.getAttributes ().put ( "severity", new Variant ( severity ) );
            if ( facility != null )
                event.getAttributes ().put ( "syslog.facility", new Variant ( facility ) );
            if ( syslogPriority != null )
                event.getAttributes ().put ( "syslog.priority", new Variant ( syslogPriority ) );
            
            _store.submitEvent ( event );
        }
        else
            _log.debug ( "did not match: '" + line + "'" );
    }
    
    protected String getNextEventId ( Calendar timestamp, String sourceName, String hostname )
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
        return hostname + "." + sourceName + "." + _lastTimestamp.getTimeInMillis () + "." + _cnt;
    }
}
