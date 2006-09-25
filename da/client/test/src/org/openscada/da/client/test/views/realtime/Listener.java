package org.openscada.da.client.test.views.realtime;

public interface Listener
{
    void added ( ListEntry [] entries );
    void updated ( ListEntry [] entries );
    void removed ( ListEntry [] entries );
}
