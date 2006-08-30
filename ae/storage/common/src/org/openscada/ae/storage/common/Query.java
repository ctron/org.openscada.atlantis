package org.openscada.ae.storage.common;


public interface Query
{
    SubscriptionReader createSubscriptionReader ( int archiveSet );
    Reader createReader ();
}
