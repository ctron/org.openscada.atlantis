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

public class SyslogHelper
{

    public static final int LOG_EMERG = 0;

    public static final int LOG_ALERT = 1;

    public static final int LOG_CRIT = 2;

    public static final int LOG_ERR = 3;

    public static final int LOG_WARNING = 4;

    public static final int LOG_NOTICE = 5;

    public static final int LOG_INFO = 6;

    public static final int LOG_DEBUG = 7;

    public static final int LOG_ALL = 8;

    public static final int LOG_KERN = 0;

    public static final int LOG_USER = 1;

    public static final int LOG_MAIL = 2;

    public static final int LOG_DAEMON = 3;

    public static final int LOG_AUTH = 4;

    public static final int LOG_SYSLOG = 5;

    public static final int LOG_LPR = 6;

    public static final int LOG_NEWS = 7;

    public static final int LOG_UUCP = 8;

    public static final int LOG_CRON = 9;

    public static final int LOG_PRIMASK = 0x0007;

    public static final int LOG_FACMASK = 0x03F8;

    public static int getPriority ( final int messageCode )
    {
        return messageCode & LOG_PRIMASK;
    }

    public static int getFacility ( final int messageCode )
    {
        return ( messageCode & LOG_FACMASK ) >> 3;
    }

    public static String getPriorityNameConverted ( final int priority )
    {
        switch ( priority )
        {
        case LOG_EMERG:
            return "FATAL";
        case LOG_ALERT:
            return "FATAL";
        case LOG_CRIT:
            return "FATAL";
        case LOG_ERR:
            return "ERROR";
        case LOG_WARNING:
            return "WARNING";
        case LOG_NOTICE:
            return "INFO";
        case LOG_INFO:
            return "INFO";
        case LOG_DEBUG:
            return "DEBUG";
        default:
            return "INFO";
        }
    }

    public static String getPriorityName ( final int priority )
    {
        switch ( priority )
        {
        case LOG_EMERG:
            return "panic";
        case LOG_ALERT:
            return "alert";
        case LOG_CRIT:
            return "critical";
        case LOG_ERR:
            return "error";
        case LOG_WARNING:
            return "warning";
        case LOG_NOTICE:
            return "notice";
        case LOG_INFO:
            return "info";
        case LOG_DEBUG:
            return "debug";
        default:
            return "INFO";
        }
    }

    public static String getFacilityName ( final int facility )
    {
        switch ( facility )
        {
        case LOG_KERN:
            return "kernel";
        case LOG_USER:
            return "user";
        case LOG_MAIL:
            return "mail";
        case LOG_DAEMON:
            return "daemon";
        case LOG_AUTH:
            return "auth";
        case LOG_SYSLOG:
            return "syslog";
        case LOG_LPR:
            return "lpr";
        case LOG_NEWS:
            return "news";
        case LOG_UUCP:
            return "uucp";
        case LOG_CRON:
            return "cron";
        default:
            return null;
        }
    }

}
