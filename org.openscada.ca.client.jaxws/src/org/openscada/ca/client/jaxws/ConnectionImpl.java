/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ca.client.jaxws;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.ca.ConfigurationInformation;
import org.openscada.ca.DiffEntry;
import org.openscada.ca.FactoryInformation;
import org.openscada.ca.client.Connection;
import org.openscada.ca.client.FactoriesListener;
import org.openscada.ca.client.jaxws.impl.ApplyDiff;
import org.openscada.ca.client.jaxws.impl.LoadConfigurationFactory;
import org.openscada.ca.client.jaxws.impl.QueryFactory;
import org.openscada.ca.client.jaxws.impl.QueryFactoryList;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.OperationException;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.utils.concurrent.FutureListener;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionImpl implements Connection
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionImpl.class );

    private ScheduledExecutorService executor;

    private final ConnectionInformation connectionInformation;

    private volatile ConnectionState state = ConnectionState.CLOSED;

    private final Set<ConnectionStateListener> stateListener = new HashSet<ConnectionStateListener> ();

    private RemoteConfigurationClient port;

    private FactoryInformation[] factories = new FactoryInformation[0];

    private final Set<FactoriesListener> factoriesListener = new HashSet<FactoriesListener> ();

    public ConnectionImpl ( final ConnectionInformation connectionInformation )
    {
        this.connectionInformation = connectionInformation;
    }

    @Override
    public Map<String, String> getSessionProperties ()
    {
        return Collections.emptyMap ();
    }

    @Override
    public synchronized void addConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        if ( this.stateListener.add ( connectionStateListener ) )
        {
            connectionStateListener.stateChange ( this, this.state, null );
        }
    }

    @Override
    public synchronized void removeConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        this.stateListener.remove ( connectionStateListener );
    }

    protected synchronized void setState ( final ConnectionState state, final Throwable e )
    {
        this.state = state;
        for ( final ConnectionStateListener listener : this.stateListener )
        {
            try
            {
                listener.stateChange ( this, state, e );
            }
            catch ( final Exception e1 )
            {
                logger.info ( "Failed to call listener", e1 );
            }
        }
    }

    @Override
    public synchronized void connect ()
    {
        if ( this.executor != null )
        {
            return;
        }

        this.executor = Executors.newSingleThreadScheduledExecutor ();
        setState ( ConnectionState.CONNECTING, null );

        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                try
                {
                    setPort ( createPort () );
                }
                catch ( final Exception e )
                {
                    setError ( e );
                }
            }
        } );
    }

    protected synchronized void setError ( final Throwable e )
    {
        disconnect ( e );
    }

    protected synchronized void setPort ( final RemoteConfigurationClient port )
    {
        if ( this.executor == null )
        {
            return;
        }

        if ( port == null )
        {
            setError ( new IllegalArgumentException ( "No port set" ).fillInStackTrace () );
            return;
        }

        this.port = port;
        setState ( ConnectionState.BOUND, null );

        final FutureTask<FactoryInformation[]> task = new FutureTask<FactoryInformation[]> ( new QueryFactoryList ( port ) );

        task.addListener ( new FutureListener<FactoryInformation[]> () {

            @Override
            public void complete ( final Future<FactoryInformation[]> future )
            {
                try
                {
                    setFactories ( future.get () );
                }
                catch ( final InterruptedException e )
                {
                    Thread.currentThread ().interrupt ();
                }
                catch ( final ExecutionException e )
                {
                    setError ( e );
                }
            }
        } );
        this.executor.submit ( task );
    }

    protected synchronized void setFactories ( final FactoryInformation[] factories )
    {
        if ( factories != null )
        {
            this.factories = factories;
        }
        else
        {
            this.factories = new FactoryInformation[0];
        }

        for ( final FactoriesListener listener : this.factoriesListener )
        {
            listener.updateFactories ( this.factories );
        }
    }

    protected RemoteConfigurationClient createPort () throws Exception
    {
        return new RemoteConfigurationClient ( this.connectionInformation.getTarget (), this.connectionInformation.getSecondaryTarget () );
    }

    @Override
    public synchronized void disconnect ()
    {
        disconnect ( null );
    }

    protected synchronized void disconnect ( final Throwable error )
    {
        if ( this.executor == null )
        {
            return;
        }

        setState ( ConnectionState.CLOSED, error );
        setFactories ( null );
        final List<Runnable> tasks = this.executor.shutdownNow ();

        // cancel all open tasks
        for ( final Runnable r : tasks )
        {
            if ( r instanceof Future<?> )
            {
                ( (Future<?>)r ).cancel ( true );
            }
        }

        this.executor = null;
        this.port = null;
    }

    @Override
    public ConnectionInformation getConnectionInformation ()
    {
        return this.connectionInformation;
    }

    @Override
    public ConnectionState getState ()
    {
        return this.state;
    }

    @Override
    public synchronized void addFactoriesListener ( final FactoriesListener listener )
    {
        if ( this.factoriesListener.add ( listener ) )
        {
            listener.updateFactories ( this.factories );
        }
    }

    @Override
    public synchronized NotifyFuture<FactoryInformation[]> getFactories ()
    {
        if ( this.executor == null )
        {
            return new InstantErrorFuture<FactoryInformation[]> ( new OperationException ( "Not connected" ) );
        }
        final QueryFactoryList call = new QueryFactoryList ( this.port );
        final FutureTask<FactoryInformation[]> task = new FutureTask<FactoryInformation[]> ( call );

        this.executor.execute ( task );

        return task;
    }

    @Override
    public synchronized NotifyFuture<FactoryInformation> getFactoryWithData ( final String factoryId )
    {
        if ( this.executor == null )
        {
            return new InstantErrorFuture<FactoryInformation> ( new OperationException ( "Not connected" ) );
        }

        final QueryFactory call = new QueryFactory ( this.port, factoryId );

        final FutureTask<FactoryInformation> task = new FutureTask<FactoryInformation> ( call );

        this.executor.execute ( task );

        return task;
    }

    @Override
    public synchronized NotifyFuture<ConfigurationInformation> getConfiguration ( final String factoryId, final String configurationId )
    {
        if ( this.executor == null )
        {
            return new InstantErrorFuture<ConfigurationInformation> ( new OperationException ( "Not connected" ) );
        }

        final LoadConfigurationFactory call = new LoadConfigurationFactory ( this.port, factoryId, configurationId );

        final FutureTask<ConfigurationInformation> task = new FutureTask<ConfigurationInformation> ( call );

        this.executor.execute ( task );

        return task;
    }

    @Override
    public synchronized NotifyFuture<Void> applyDiff ( final Collection<DiffEntry> changeSet )
    {
        if ( this.executor == null )
        {
            return new InstantErrorFuture<Void> ( new OperationException ( "Not connected" ) );
        }

        final ApplyDiff call = new ApplyDiff ( this.port, changeSet );

        final FutureTask<Void> task = new FutureTask<Void> ( call );

        this.executor.execute ( task );

        return task;
    }

    @Override
    public synchronized void removeFactoriesListener ( final FactoriesListener listener )
    {
        this.factoriesListener.remove ( listener );
    }

}
