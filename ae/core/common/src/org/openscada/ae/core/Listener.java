package org.openscada.ae.core;

public interface Listener
{
    void added ( AttributedIdentifier[] events );
    void removed ( String[] eventIDs );
    void modified ( AttributedIdentifier[] events );
}
