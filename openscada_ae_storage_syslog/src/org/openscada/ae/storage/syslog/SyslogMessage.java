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
        EMERGENCY(0),
        ALERT(1),
        CRITICAL(2),
        ERROR(3),
        WARNING(4),
        NOTICE(5),
        INFO(6),
        DEBUG(7),
        ;
        
        int _code;
        Priority ( int code )
        {
            _code = code;
        }
        public int getCode ()
        {
            return _code;
        }
        
    };
    
    public enum Facility
    {
        KERNEL(0),
        USER(1),
        MAIL(2),
        DAEMON(3),
        AUTH(4),
        SYSOG(5),
        PR(6),
        NEWS(7),
        UUCP(8),
        CRON(9),
        ;
        
        int _code;
        Facility ( int code )
        {
            _code = code;
        }
        
        public int getCode ()
        {
            return _code;
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
        return _facility;
    }
    public void setFacility ( Facility facility )
    {
        _facility = facility;
    }
    public String getMessage ()
    {
        return _message;
    }
    public void setMessage ( String message )
    {
        _message = message;
    }
    public Priority getPriority ()
    {
        return _priority;
    }
    public void setPriority ( Priority priority )
    {
        _priority = priority;
    }
    public Calendar getTimestamp ()
    {
        return _timestamp;
    }
    public void setTimestamp ( Calendar timestamp )
    {
        _timestamp = timestamp;
    }
    public String getHost ()
    {
        return _host;
    }
    public void setHost ( String host )
    {
        _host = host;
    }
    public String getApplication ()
    {
        return _application;
    }
    public void setApplication ( String application )
    {
        _application = application;
    }
    public Long getProcessId ()
    {
        return _processId;
    }
    public void setProcessId ( Long processId )
    {
        _processId = processId;
    }
    
    
}
