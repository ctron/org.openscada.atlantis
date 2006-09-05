package org.openscada.ae.storage.syslog;

import org.openscada.ae.core.Event;

public interface DataStore
{
    public void submitEvent ( Event event ); 
}
