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

package org.openscada.ca.client.ngp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.openscada.ca.client.Connection;
import org.openscada.ca.client.FactoriesListener;
import org.openscada.ca.common.ngp.ProtocolConfigurationFactoryImpl;
import org.openscada.ca.data.ConfigurationInformation;
import org.openscada.ca.data.DiffEntry;
import org.openscada.ca.data.FactoryInformation;
import org.openscada.ca.data.message.ApplyDiffRequest;
import org.openscada.ca.data.message.ApplyDiffResponse;
import org.openscada.ca.data.message.ErrorResponse;
import org.openscada.ca.data.message.GetConfigurationRequest;
import org.openscada.ca.data.message.GetConfigurationResponse;
import org.openscada.ca.data.message.GetFactoriesRequest;
import org.openscada.ca.data.message.GetFactoriesResponse;
import org.openscada.ca.data.message.GetFactoryWithDataRequest;
import org.openscada.ca.data.message.GetFactoryWithDataResponse;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.OperationException;
import org.openscada.core.client.ngp.ConnectionBaseImpl;
import org.openscada.core.data.ResponseMessage;
import org.openscada.utils.concurrent.ExecutorFuture;
import org.openscada.utils.concurrent.FutureListener;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionImpl extends ConnectionBaseImpl implements Connection
{
    private final static Logger logger = LoggerFactory.getLogger ( ConnectionImpl.class );

    protected static abstract class ErrorAwareFuture<Res extends ResponseMessage, M> extends ExecutorFuture<M>
    {
        public ErrorAwareFuture ( final Executor executor, final Class<Res> responseClazz, final NotifyFuture<ResponseMessage> listener )
        {
            super ( executor );
            listener.addListener ( new FutureListener<ResponseMessage> () {
                @Override
                public void complete ( final Future<ResponseMessage> future )
                {
                    try
                    {
                        final ResponseMessage result = future.get ();

                        if ( responseClazz.isAssignableFrom ( result.getClass () ) )
                        {
                            asyncSetResult ( handleResponse ( responseClazz.cast ( result ) ) );
                        }
                        else if ( result instanceof ErrorResponse )
                        {
                            asyncSetError ( new OperationException ( ( (ErrorResponse)result ).getErrorInformation ().getMessage () ) );
                        }
                        else
                        {
                            asyncSetError ( new OperationException ( String.format ( "Wrong reply in message. Expected: %s, Received: %s", responseClazz.getClass (), result.getClass () ) ) );
                        }
                    }
                    catch ( final Exception e )
                    {
                        asyncSetError ( e );
                    }
                }
            } );
        }

        public abstract M handleResponse ( final Res response ) throws Exception;

    }

    private final Set<FactoriesListener> listeners = new HashSet<FactoriesListener> ();

    private FactoryInformation[] factories = new FactoryInformation[0];

    public ConnectionImpl ( final ConnectionInformation connectionInformation ) throws Exception
    {
        super ( new ProtocolConfigurationFactoryImpl ( connectionInformation ), connectionInformation );
    }

    @Override
    protected void onConnectionBound ()
    {
        super.onConnectionBound ();
        getFactories ().addListener ( new FutureListener<FactoryInformation[]> () {

            @Override
            public void complete ( final Future<FactoryInformation[]> future )
            {
                try
                {
                    setFactories ( future.get () );
                }
                catch ( final Exception e )
                {
                    logger.warn ( "Failed to load initial factories list", e );
                }
            }
        } );
    }

    @Override
    protected void onConnectionClosed ()
    {
        setFactories ( new FactoryInformation[0] );
        super.onConnectionClosed ();
    }

    protected synchronized void setFactories ( final FactoryInformation[] factories )
    {
        this.factories = factories;
        for ( final FactoriesListener listener : this.listeners )
        {
            this.executor.execute ( new Runnable () {

                @Override
                public void run ()
                {
                    listener.updateFactories ( factories );
                }
            } );
        }
    }

    @Override
    public synchronized void addFactoriesListener ( final FactoriesListener listener )
    {
        if ( this.listeners.add ( listener ) )
        {
            final FactoryInformation[] factories = this.factories;

            this.executor.execute ( new Runnable () {
                @Override
                public void run ()
                {
                    listener.updateFactories ( factories );
                };
            } );
        }
    }

    @Override
    public synchronized void removeFactoriesListener ( final FactoriesListener listener )
    {
        this.listeners.remove ( listener );
    }

    @Override
    public synchronized NotifyFuture<FactoryInformation[]> getFactories ()
    {
        logger.debug ( "Getting all factories" );

        return new ErrorAwareFuture<GetFactoriesResponse, FactoryInformation[]> ( this.executor, GetFactoriesResponse.class, sendRequestMessage ( new GetFactoriesRequest ( nextRequest () ) ) ) {
            @Override
            public FactoryInformation[] handleResponse ( final GetFactoriesResponse result )
            {
                return result.getFactories ().toArray ( new FactoryInformation[result.getFactories ().size ()] );
            }
        };
    }

    @Override
    public synchronized NotifyFuture<FactoryInformation> getFactoryWithData ( final String factoryId )
    {
        logger.debug ( "Getting factory: {}", factoryId );
        return new ErrorAwareFuture<GetFactoryWithDataResponse, FactoryInformation> ( this.executor, GetFactoryWithDataResponse.class, sendRequestMessage ( new GetFactoryWithDataRequest ( nextRequest (), factoryId ) ) ) {
            @Override
            public FactoryInformation handleResponse ( final GetFactoryWithDataResponse result )
            {
                return result.getFactory ();
            }
        };
    }

    @Override
    public synchronized NotifyFuture<ConfigurationInformation> getConfiguration ( final String factoryId, final String configurationId )
    {
        logger.debug ( "Getting configuration: {} - {}", factoryId, configurationId );

        return new ErrorAwareFuture<GetConfigurationResponse, ConfigurationInformation> ( this.executor, GetConfigurationResponse.class, sendRequestMessage ( new GetConfigurationRequest ( nextRequest (), factoryId, configurationId ) ) ) {
            @Override
            public ConfigurationInformation handleResponse ( final GetConfigurationResponse result )
            {
                return result.getConfiguration ();
            }
        };
    }

    @Override
    public NotifyFuture<Void> applyDiff ( final List<DiffEntry> changeSet )
    {
        logger.debug ( "Apply diff: {} changes", changeSet.size () );

        return new ErrorAwareFuture<ApplyDiffResponse, Void> ( this.executor, ApplyDiffResponse.class, sendRequestMessage ( new ApplyDiffRequest ( nextRequest (), changeSet ) ) ) {
            @Override
            public Void handleResponse ( final ApplyDiffResponse result )
            {
                // TODO: provide real response
                return null;
            }
        };
    }
}
