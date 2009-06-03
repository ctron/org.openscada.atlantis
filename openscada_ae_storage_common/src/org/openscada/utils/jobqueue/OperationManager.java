/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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
        private Operation operation = null;

        private OperationManager manager = null;

        private Long id = null;

        private boolean started = false;

        private boolean canceled = false;

        public Handle ( final Operation operation, final OperationManager manager, final Long id )
        {
            this.operation = operation;
            this.manager = manager;
            this.id = id;
        }

        public void cancel () throws CancelNotSupportedException
        {
            synchronized ( this )
            {
                if ( !this.started )
                {
                    return;
                }
                this.canceled = true;
            }
            this.operation.cancel ();
            remove ();
        }

        public void start ()
        {
            synchronized ( this )
            {
                if ( this.started || this.canceled )
                {
                    return;
                }
                this.started = true;
            }
            this.operation.start ( this );
        }

        public void completed ()
        {
            remove ();
        }

        private synchronized void remove ()
        {
            this.manager.remove ( this );
            notifyAll ();
        }

        public Long getId ()
        {
            return this.id;
        }
    }

    public interface Listener
    {
        void removedHandle ( Handle handle );
    }

    private final Set<Listener> listeners = new CopyOnWriteArraySet<Listener> ();

    private final Map<Long, Handle> operationMap = new HashMap<Long, Handle> ();

    public synchronized Handle schedule ( final Operation operation )
    {
        final Random r = new Random ();
        Long id;
        do
        {
            id = r.nextLong ();
        } while ( this.operationMap.containsKey ( id ) );

        final Handle handle = new Handle ( operation, this, id );
        this.operationMap.put ( id, handle );
        return handle;
    }

    public void remove ( final Handle handle )
    {
        synchronized ( this )
        {
            this.operationMap.remove ( handle.getId () );
            fireRemoved ( handle );
        }
    }

    public Handle get ( final long id )
    {
        synchronized ( this )
        {
            return this.operationMap.get ( id );
        }
    }

    public void addListener ( final Listener listener )
    {
        this.listeners.add ( listener );
    }

    public void removeListener ( final Listener listener )
    {
        this.listeners.remove ( listener );
    }

    private void fireRemoved ( final Handle handle )
    {
        for ( final Listener listener : this.listeners )
        {
            listener.removedHandle ( handle );
        }
    }

}
