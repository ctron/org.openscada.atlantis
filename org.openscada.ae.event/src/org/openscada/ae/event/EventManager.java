package org.openscada.ae.event;

/**
 * A manager which generates events and provided the interformation to its listeners
 * @author Jens Reimann
 *
 */
public interface EventManager
{
    /**
     * Add this listener to the manager
     * <p>
     * If the listener was already added the request will be ignored.
     * </p>
     * <p>
     * If the listener was added to the manager, all current known events
     * have to be provided to the listener.
     * </p>
     * 
     * @param listener the listener to add
     */
    public void addEventListener ( EventListener listener );

    public void removeEventListener ( EventListener listener );
}
