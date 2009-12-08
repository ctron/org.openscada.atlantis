package org.openscada.ae.event;

import org.openscada.ae.Event;

/**
 * An interface to listen for AE events
 * @author Jens Reimann
 *
 */
public interface EventListener
{
    /**
     * Handle the provided events
     * @param event the events to handle
     */
    public void handleEvent ( Event[] event );
}
