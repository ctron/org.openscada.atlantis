package org.openscada.da.client.viewer.model.impl;

import java.util.HashSet;
import java.util.Set;

import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.OutputListener;
import org.openscada.da.client.viewer.model.Type;

public abstract class BaseOutput implements OutputDefinition
{

    private String _name = null;
    private Set<OutputListener> _listeners = new HashSet<OutputListener> ();
    
    private Type _lastType = Type.NULL;
    private Object _lastValue = null;

    public BaseOutput ( String name )
    {
        super ();
        _name = name;
    }

    public synchronized void addListener ( OutputListener listener )
    {
        _listeners.add ( listener );
        listener.update ( _lastType, _lastValue );
    }

    public synchronized void removeListener ( OutputListener listener )
    {
        _listeners.remove ( listener );
    }
    
    public synchronized boolean hasListeners ()
    {
        return _listeners.size () > 0;
    }

    public String getName ()
    {
        return _name;
    }
    
    protected synchronized void fireEvent ( Type type, Object value )
    {
        _lastType = type;
        _lastValue = value;
        
        for ( OutputListener listener : _listeners )
        {
            listener.update ( type, value );
        }
    }

}