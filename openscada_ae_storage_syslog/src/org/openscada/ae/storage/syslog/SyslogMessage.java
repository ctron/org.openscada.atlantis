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

package org.openscada.ae.storage.syslog;

import java.util.Calendar;

public class SyslogMessage
{
    public enum Priority
    {
        EMERGENCY ( 0 ),
        ALERT ( 1 ),
        CRITICAL ( 2 ),
        ERROR ( 3 ),
        WARNING ( 4 ),
        NOTICE ( 5 ),
        INFO ( 6 ),
        DEBUG ( 7 ), ;

        int _code;

        Priority ( final int code )
        {
            this._code = code;
        }

        public int getCode ()
        {
            return this._code;
        }

    };

    public enum Facility
    {
        KERNEL ( 0 ),
        USER ( 1 ),
        MAIL ( 2 ),
        DAEMON ( 3 ),
        AUTH ( 4 ),
        SYSOG ( 5 ),
        PR ( 6 ),
        NEWS ( 7 ),
        UUCP ( 8 ),
        CRON ( 9 ), ;

        int _code;

        Facility ( final int code )
        {
            this._code = code;
        }

        public int getCode ()
        {
            return this._code;
        }
    };

    private String _message = "";

    private String _host = "localhost";

    private String _application = "";

    private Calendar _timestamp = Calendar.getInstance ();

    private Priority _priority = Priority.INFO;

    private Facility _facility = Facility.USER;

    private Long _processId = null;

    public Facility getFacility ()
    {
        return this._facility;
    }

    public void setFacility ( final Facility facility )
    {
        this._facility = facility;
    }

    public String getMessage ()
    {
        return this._message;
    }

    public void setMessage ( final String message )
    {
        this._message = message;
    }

    public Priority getPriority ()
    {
        return this._priority;
    }

    public void setPriority ( final Priority priority )
    {
        this._priority = priority;
    }

    public Calendar getTimestamp ()
    {
        return this._timestamp;
    }

    public void setTimestamp ( final Calendar timestamp )
    {
        this._timestamp = timestamp;
    }

    public String getHost ()
    {
        return this._host;
    }

    public void setHost ( final String host )
    {
        this._host = host;
    }

    public String getApplication ()
    {
        return this._application;
    }

    public void setApplication ( final String application )
    {
        this._application = application;
    }

    public Long getProcessId ()
    {
        return this._processId;
    }

    public void setProcessId ( final Long processId )
    {
        this._processId = processId;
    }

}
