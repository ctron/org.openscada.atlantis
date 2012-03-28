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

package org.openscada.ae.server.common;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.openscada.ae.Event;
import org.openscada.ae.Query;
import org.openscada.ae.QueryListener;
import org.openscada.ae.QueryState;
import org.openscada.ae.server.storage.Storage;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryImpl implements Query
{

    private final static Logger logger = LoggerFactory.getLogger ( QueryImpl.class );

    private boolean disposed = false;

    private final SessionImpl session;

    private final Executor eventExecutor;

    private final QueryListener listener;

    private QueryState currentState;

    private final ExecutorService loadExecutor;

    private final SingleServiceTracker<Storage> tracker;

    private Storage storage;

    private boolean initLoad = true;

    private volatile Future<?> initialLoadJob;

    private volatile Future<?> loadJob;

    @SuppressWarnings ( "unused" )
    private final String queryType;

    private final String queryData;

    private org.openscada.ae.server.storage.Query query;

    public QueryImpl ( final BundleContext context, final SessionImpl sessionImpl, final Executor eventExecutor, final ExecutorService loadExecutor, final String queryType, final String queryData, final QueryListener listener )
    {
        this.session = sessionImpl;
        this.loadExecutor = loadExecutor;
        this.eventExecutor = eventExecutor;
        this.listener = listener;
        this.queryType = queryType;
        this.queryData = queryData;

        this.tracker = new SingleServiceTracker<Storage> ( context, Storage.class, new SingleServiceListener<Storage> () {

            @Override
            public void serviceChange ( final ServiceReference<Storage> reference, final Storage service )
            {
                QueryImpl.this.setStorage ( service );
            }
        } );
    }

    protected synchronized void setStorage ( final Storage service )
    {
        logger.debug ( "Set storage: {}", service );

        if ( this.disposed )
        {
            return;
        }

        this.storage = service;
        if ( this.storage == null )
        {
            dispose ( null );
        }
        else
        {
            if ( this.initLoad )
            {
                this.initLoad = false;
                loadInitial ();
            }
        }
    }

    @Override
    public synchronized void close ()
    {
        dispose ( null );
    }

    private void loadInitial ()
    {
        this.initialLoadJob = this.loadExecutor.submit ( new Runnable () {
            @Override
            public void run ()
            {
                try
                {
                    QueryImpl.this.query = QueryImpl.this.storage.query ( QueryImpl.this.queryData );
                }
                catch ( final Exception e )
                {
                    logger.warn ( "Failed to query storage", e );
                }

                if ( QueryImpl.this.query == null )
                {
                    dispose ( null );
                }
                else
                {
                    startLoad ( Integer.getInteger ( "org.openscada.ae.server.common.loadinitial", 500 ) );
                }
            }
        } );
    }

    private synchronized void startLoad ( final int count )
    {
        logger.debug ( "Starting to load {}", count );

        this.loadJob = this.loadExecutor.submit ( new Runnable () {

            @Override
            public void run ()
            {
                QueryImpl.this.performLoad ( count );
            }
        } );
    }

    /**
     * Perform the actual load process
     * @param count number of entries to load
     */
    protected void performLoad ( final int count )
    {
        try
        {
            logger.debug ( "Calling get next: {}...", count );
            final Collection<Event> result = this.query.getNext ( count );
            logger.debug ( "Calling get next: {}... complete", count );

            this.eventExecutor.execute ( new Runnable () {

                @Override
                public void run ()
                {
                    QueryImpl.this.listener.queryData ( result.toArray ( new Event[result.size ()] ) );
                }
            } );

            if ( result.size () < count )
            {
                logger.info ( "Reached end of query: {}", result.size () );
                dispose ( null );
            }
            else
            {
                setState ( QueryState.CONNECTED, null );
            }

        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to load data", e );
            synchronized ( this )
            {
                this.loadJob = null;
                dispose ( e );
            }
        }
        finally
        {
            synchronized ( this )
            {
                this.loadJob = null;
            }
        }
    }

    @Override
    public synchronized void loadMore ( final int count )
    {
        if ( this.loadJob != null )
        {
            // we already are currently loading
            return;
        }

        setState ( QueryState.LOADING, null );
        startLoad ( count );
    }

    public synchronized void start ()
    {
        setState ( QueryState.LOADING, null );
        this.tracker.open ();
    }

    private void setState ( final QueryState state, final Throwable error )
    {
        if ( this.currentState == state )
        {
            return;
        }

        this.currentState = state;
        this.eventExecutor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                QueryImpl.this.listener.queryStateChanged ( state, error );
            }
        } );
    }

    public void dispose ( final Throwable error )
    {
        synchronized ( this )
        {
            if ( this.disposed )
            {
                return;
            }

            this.disposed = true;

            if ( this.initialLoadJob != null )
            {
                this.initialLoadJob.cancel ( true );
                this.initialLoadJob = null;
            }

            if ( this.loadJob != null )
            {
                this.loadJob.cancel ( true );
                this.loadJob = null;
            }

            if ( this.query != null )
            {
                this.query.dispose ();
                this.query = null;
            }

            setState ( QueryState.DISCONNECTED, error );
            this.tracker.close ();
        }

        this.session.removeQuery ( this );
    }

    public synchronized boolean isDisposed ()
    {
        return this.disposed;
    }

    @Override
    protected void finalize () throws Throwable
    {
        logger.debug ( "Disposed query: {}", this );
        super.finalize ();
    }

}
