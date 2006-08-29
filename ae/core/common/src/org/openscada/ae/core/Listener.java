package org.openscada.ae.core;

public interface Listener
{
    void added ( Event[] events );
    void removed ( String[] eventIDs );
    void modified ( Event[] events );
}
