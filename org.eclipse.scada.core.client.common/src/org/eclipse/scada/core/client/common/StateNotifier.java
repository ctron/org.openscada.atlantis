/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.eclipse.scada.core.client.common;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.scada.core.client.Connection;
import org.eclipse.scada.core.client.ConnectionState;
import org.eclipse.scada.core.client.ConnectionStateListener;

public class StateNotifier
{
    private volatile boolean disposed;

    private final ExecutorService executor;

    private final AtomicReference<Set<ConnectionStateListener>> connectionStateListeners = new AtomicReference<Set<ConnectionStateListener>> ( Collections.<ConnectionStateListener> emptySet () );

    private final Connection connection;

    public StateNotifier ( final ExecutorService executor, final Connection connection )
    {
        this.executor = executor;
        this.connection = connection;
    }

    public void fireConnectionStateChange ( final ConnectionState connectionState, final Throwable error )
    {
        if ( this.disposed )
        {
            return;
        }

        this.executor.execute ( new Runnable () {
            @Override
            public void run ()
            {
                doFireConnectionStateChange ( connectionState, error );
            };
        } );
    }

    protected void doFireConnectionStateChange ( final ConnectionState connectionState, final Throwable error )
    {
        if ( this.disposed )
        {
            return;
        }

        final Set<ConnectionStateListener> listeners = this.connectionStateListeners.get ();
        for ( final ConnectionStateListener listener : listeners )
        {
            listener.stateChange ( this.connection, connectionState, error );
        }
    }

    public void addConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        Set<ConnectionStateListener> update;
        Set<ConnectionStateListener> expect;
        do
        {
            expect = this.connectionStateListeners.get ();
            update = new LinkedHashSet<ConnectionStateListener> ( this.connectionStateListeners.get () );
            update.add ( connectionStateListener );
        } while ( !this.connectionStateListeners.compareAndSet ( expect, update ) );
    }

    public void removeConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        Set<ConnectionStateListener> update;
        Set<ConnectionStateListener> expect;
        do
        {
            expect = this.connectionStateListeners.get ();
            update = new LinkedHashSet<ConnectionStateListener> ( this.connectionStateListeners.get () );
            update.remove ( connectionStateListener );
        } while ( !this.connectionStateListeners.compareAndSet ( expect, update ) );
    }

    public void dispose ()
    {
        this.disposed = true;
    }

}
