package org.openscada.ae.event;

import org.openscada.ae.Event;

/**
 * A service which will handle events
 * @author Jens Reimann
 *
 */
public interface EventService
{
    /**
     * Publish the provided event
     * @param event the event to publish
     */
    public void publishEvent ( Event event );
}
