package org.openscada.ae.storage.common;

import org.openscada.ae.core.Event;


public interface Query
{
    SubscriptionReader createSubscriptionReader ( int archiveSet );
    Reader createReader ();
    
    void submitEvent ( Event event );
}
