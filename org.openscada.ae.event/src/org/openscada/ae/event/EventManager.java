package org.openscada.ae.event;

public interface EventManager
{
    public void addEventListener ( EventListener listener );

    public void removeEventListener ( EventListener listener );
}
