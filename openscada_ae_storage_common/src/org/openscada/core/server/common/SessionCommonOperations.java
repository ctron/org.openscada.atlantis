/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 to 2009 inavare GmbH (http://inavare.com)
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

package org.openscada.core.server.common;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;
import org.openscada.utils.jobqueue.CancelNotSupportedException;
import org.openscada.utils.jobqueue.OperationManager;
import org.openscada.utils.jobqueue.OperationManager.Handle;

public class SessionCommonOperations implements OperationManager.Listener
{
    private static Logger logger = Logger.getLogger ( SessionCommonOperations.class );

    private final Set<Handle> operations = new CopyOnWriteArraySet<Handle> ();

    public boolean addOperation ( Handle handle )
    {
        return operations.add ( handle );
    }

    public boolean removeOperation ( Handle handle )
    {
        return operations.remove ( handle );
    }

    public boolean containsOperation ( Handle handle )
    {
        return operations.contains ( handle );
    }

    public void removedHandle ( Handle handle )
    {
        removeOperation ( handle );
    }

    public Set<Handle> getOperations ()
    {
        return new HashSet<Handle> ( operations );
    }

    public void clear ()
    {
        operations.clear ();
    }

    public void cancelAll ()
    {
        Set<Handle> ops = new HashSet<Handle> ( operations );
        operations.clear ();

        // cancel all pending operations
        for ( Handle handle : ops )
        {
            try
            {
                logger.info ( "Stopping operation: " + handle );
                handle.cancel ();
            }
            catch ( CancelNotSupportedException e )
            {
                logger.warn ( "Failed to cancel job on session destruction", e );
                // ignore it .. we can't do anything
            }
        }
    }
}
