package org.openscada.da.core.common.impl;

import java.util.HashSet;
import java.util.Set;

import org.openscada.utils.jobqueue.OperationManager.Handle;
import org.openscada.utils.jobqueue.OperationManager;

public class SessionCommonOperations implements OperationManager.Listener
{
    private Set<Handle> _operations = new HashSet<Handle> (); 
    
    synchronized public boolean addOperation ( Handle handle )
    {
        return _operations.add ( handle );
    }
    
    synchronized public boolean removeOperation ( Handle handle )
    {
        return _operations.remove ( handle );
    }
    
    synchronized public boolean containsOperation ( Handle handle )
    {
        return _operations.contains ( handle );
    }

    public void removedHandle ( Handle handle )
    {
        removeOperation ( handle );
    }

    synchronized public Set<Handle> getOperations ()
    {
        return new HashSet<Handle> ( _operations );
    }

    synchronized public void clear ()
    {
        _operations.clear ();
    }
}
