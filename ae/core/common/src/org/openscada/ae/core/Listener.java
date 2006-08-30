package org.openscada.ae.core;

public interface Listener
{
    void events ( EventInformation[] events );
    void unsubscribed ( String reason );
}
