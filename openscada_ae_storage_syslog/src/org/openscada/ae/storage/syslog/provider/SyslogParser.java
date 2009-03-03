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

    private final DateParser _dateParser = new SyslogDateParser ();

    private Calendar _lastTimestamp = null;

    private long _cnt = 0;

    public SyslogParser ( final DataStore store, final String sourceName, final String severity )
    {
        super ();
        this._store = store;
        this._sourceName = sourceName;
        this._defaultSeverity = severity;

        this._pattern = Pattern.compile ( "(\\<([0-9]+)\\>|)(.*?[0-9]{2}:[0-9]{2}:[0-9]{2})\\s+([a-zA-Z0-9\\.-]+)\\s+(([\\w\\S]+):\\s*|)(.*)" );
    }

    public void handleLine ( final String line )
    {
        final Matcher matcher = this._pattern.matcher ( line );
        if ( matcher.matches () )
        {
            String severity = this._defaultSeverity;
            String facility = null;
            String syslogPriority = null;

            try
            {
                final int messageCode = Integer.parseInt ( matcher.group ( 2 ) );
                severity = SyslogHelper.getPriorityNameConverted ( SyslogHelper.getPriority ( messageCode ) );
                facility = SyslogHelper.getFacilityName ( SyslogHelper.getFacility ( messageCode ) );
                syslogPriority = SyslogHelper.getPriorityName ( SyslogHelper.getPriority ( messageCode ) );
            }
            catch ( final Exception e )
            {
            }

            final String timestamp = matcher.group ( 3 );
            final String host = matcher.group ( 4 );
            final String app = matcher.group ( 6 );
            final String message = matcher.group ( 7 );

            final Calendar datetime = this._dateParser.parseDate ( timestamp );

            final Event event = new Event ( getNextEventId ( datetime, this._sourceName, host ) );

            event.setTimestamp ( this._dateParser.parseDate ( timestamp ) );
            event.getAttributes ().put ( "syslog.raw.timestamp", new Variant ( timestamp ) );
            event.getAttributes ().put ( "host", new Variant ( host ) );
            event.getAttributes ().put ( "application", new Variant ( app ) );
            event.getAttributes ().put ( "message", new Variant ( message ) );
            event.getAttributes ().put ( "raw", new Variant ( matcher.group ( 0 ) ) );
            event.getAttributes ().put ( "source", new Variant ( this._sourceName ) );
            event.getAttributes ().put ( "severity", new Variant ( severity ) );
            if ( facility != null )
            {
                event.getAttributes ().put ( "syslog.facility", new Variant ( facility ) );
            }
            if ( syslogPriority != null )
            {
                event.getAttributes ().put ( "syslog.priority", new Variant ( syslogPriority ) );
            }

            this._store.submitEvent ( event );
        }
        else
        {
            _log.debug ( "did not match: '" + line + "'" );
        }
    }

    protected String getNextEventId ( final Calendar timestamp, final String sourceName, final String hostname )
    {
        if ( this._lastTimestamp == null )
        {
            this._cnt = 0;
            this._lastTimestamp = timestamp;
        }
        else if ( this._lastTimestamp.equals ( timestamp ) )
        {
            this._cnt++;
        }
        else
        {
            this._lastTimestamp = timestamp;
            this._cnt = 0;
        }
        return hostname + "." + sourceName + "." + this._lastTimestamp.getTimeInMillis () + "." + this._cnt;
    }
}
