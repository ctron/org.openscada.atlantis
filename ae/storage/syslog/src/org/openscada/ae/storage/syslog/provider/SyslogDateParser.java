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
