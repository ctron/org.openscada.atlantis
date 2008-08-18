/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

package org.openscada.utils.jobqueue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class OperationManager
{
    public class Handle
    {
        private Operation _operation = null;

        private OperationManager _manager = null;

        private Long _id = null;

        private boolean _started = false;

        private boolean _canceled = false;

        public Handle ( Operation operation, OperationManager manager, Long id )
        {
            _operation = operation;
            _manager = manager;
            _id = id;
        }

        public void cancel () throws CancelNotSupportedException
        {
            synchronized ( this )
            {
                if ( !_started )
                {
                    return;
                }
                _canceled = true;
            }
            _operation.cancel ();
            remove ();
        }

        public void start ()
        {
            synchronized ( this )
            {
                if ( _started || _canceled )
                {
                    return;
                }
                _started = true;
            }
            _operation.start ( this );
        }

        public void completed ()
        {
            remove ();
        }

        private synchronized void remove ()
        {
            _manager.remove ( this );
            notifyAll ();
        }

        public Long getId ()
        {
            return _id;
        }
    }

    public interface Listener
    {
        void removedHandle ( Handle handle );
    }

    private Set<Listener> _listeners = new CopyOnWriteArraySet<Listener> ();

    private Map<Long, Handle> _operationMap = new HashMap<Long, Handle> ();

    public synchronized Handle schedule ( Operation operation )
    {
        Random r = new Random ();
        Long id;
        do
        {
            id = r.nextLong ();
        } while ( _operationMap.containsKey ( id ) );

        Handle handle = new Handle ( operation, this, id );
        _operationMap.put ( id, handle );
        return handle;
    }

    public void remove ( Handle handle )
    {
        synchronized ( this )
        {
            _operationMap.remove ( handle.getId () );
            fireRemoved ( handle );
        }
    }

    public Handle get ( long id )
    {
        synchronized ( this )
        {
            return _operationMap.get ( id );
        }
    }

    public void addListener ( Listener listener )
    {
        _listeners.add ( listener );
    }

    public void removeListener ( Listener listener )
    {
        _listeners.remove ( listener );
    }

    private void fireRemoved ( Handle handle )
    {
        for ( Listener listener : _listeners )
        {
            listener.removedHandle ( handle );
        }
    }

}
