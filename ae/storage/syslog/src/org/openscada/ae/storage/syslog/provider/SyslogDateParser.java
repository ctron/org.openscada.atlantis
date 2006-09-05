package org.openscada.ae.storage.syslog.provider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.log4j.Logger;

public class SyslogDateParser implements DateParser
{
    private static Logger _log = Logger.getLogger ( SyslogDateParser.class );
    
    private String _format = "MMM d HH:mm:ss";
    private boolean _fixYear = true;
    
    public Calendar parseDate ( String date )
    {
        try
        {
            Calendar timestamp = Calendar.getInstance ();
            DateFormat df = new SimpleDateFormat ( _format, Locale.US );
            timestamp.setTime ( df.parse ( date ) );
            
            if ( _fixYear )
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
