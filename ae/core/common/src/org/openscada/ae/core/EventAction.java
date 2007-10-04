package org.openscada.ae.core;

public enum EventAction
{
    NONE(-1),
    ADDED(0),
    MODIFIED(1),
    REMOVED(2);
    
    private int _id;
    
    private EventAction ( int id )
    {
        _id = id;
    }
    
    public int getId ()
    {
        return _id;
    }
    
    public static EventAction asAction ( int id )
    {
        for ( EventAction action : EventAction.values () )
        {
            if ( action.getId () == id )
            {
                return action;
            }
        }
        return EventAction.NONE;
    }
}
