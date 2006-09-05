package org.openscada.ae.storage.syslog.provider;

import java.util.Calendar;

public interface DateParser
{
    public Calendar parseDate ( String date );
}
