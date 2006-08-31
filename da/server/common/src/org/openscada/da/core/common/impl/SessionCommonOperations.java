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

package org.openscada.da.core.common.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.utils.jobqueue.CancelNotSupportedException;
import org.openscada.utils.jobqueue.OperationManager;
import org.openscada.utils.jobqueue.OperationManager.Handle;

public class SessionCommonOperations implements OperationManager.Listener
{
    private static Logger _log = Logger.getLogger ( SessionCommonOperations.class );
    
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
    
    synchronized public void cancelAll ()
    {
        // cancel all pending operations
        for ( Handle handle : _operations )
        {
            try
            {
                _log.info ( "Stopping operation: " + handle );
                handle.cancel ();
            }
            catch ( CancelNotSupportedException e )
            {
                _log.warn ( "Failed to cancel job on session destruction", e );
                // ignore it .. we can't do anything
            }
        }
        clear ();
    }
}
