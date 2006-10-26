/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

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