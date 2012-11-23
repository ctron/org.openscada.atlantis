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

package org.openscada.hd.server.storage.common;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.data.QueryParameters;
import org.openscada.hds.ValueVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryImpl implements Query
{

    private final static Logger logger = LoggerFactory.getLogger ( QueryImpl.class );

    private final ValueSourceManager storage;

    private final ExecutorService executor;

    private final QueryListener listener;

    private final boolean updateData;

    private final QueryBuffer buffer;

    private static class LoadState
    {
        private final boolean loading;

        private final boolean closed;

        private final QueryParameters parameters;

        public LoadState ( final boolean closed, final boolean loading, final QueryParameters parameters )
        {
            super ();
            this.closed = closed;
            this.loading = loading;

            this.parameters = parameters;
        }

        public QueryParameters getParameters ()
        {
            return this.parameters;
        }

        public boolean isLoading ()
        {
            return this.loading;
        }

        public boolean isClosed ()
        {
            return this.closed;
        }

        @Override
        public String toString ()
        {
            return String.format ( "[loading: %s, closed: %s, parameters: %s]", this.loading, this.closed, this.parameters );
        }
    }

    private final AtomicReference<LoadState> state = new AtomicReference<LoadState> ();

    /**
     * Create a new common query
     * 
     * @param storage
     *            the value source manager
     * @param executor
     *            a single threaded executor for posting events
     * @param parameters
     *            the initial query parameters
     * @param listener
     *            the query listener, must not be <code>null</code>
     * @param updateData
     *            request data updates
     * @param fixedStartDate
     *            an optional fixed start date before which all query data is
     *            invalid
     * @param fixedEndDate
     *            an optional fixed end date after which all query data is
     *            invalid
     */
    public QueryImpl ( final ValueSourceManager storage, final ScheduledExecutorService executor, final QueryParameters parameters, final QueryListener listener, final boolean updateData, final Date fixedStartDate, final Date fixedEndDate )
    {
        this.storage = storage;
        this.executor = executor;
        this.listener = listener;
        this.updateData = updateData;

        this.buffer = new QueryBuffer ( this.listener, executor, fixedStartDate, fixedEndDate );

        this.state.set ( new LoadState ( false, false, parameters ) );

        changeParameters ( parameters );
    }

    @Override
    public void close ()
    {
        if ( !requestClose () )
        {
            return;
        }

        // we should wait here for the close to be acknowledged by the loader
        while ( this.state.get ().isLoading () )
        {
            logger.debug ( "Waiting for loader to complete" );
            try
            {
                // FIXME: waiting should not be time based
                Thread.sleep ( 100 );
            }
            catch ( final InterruptedException e )
            {
                logger.warn ( "Got interrupted while waiting for loader to complete", e );
                break;
            }
        }

        this.buffer.close ();
        this.storage.queryClosed ( this );
    }

    /**
     * Request a close of the query
     * 
     * @return <code>true</code> if the close was requested, <code>false</code>
     *         if the close already was requested by someone else
     */
    private boolean requestClose ()
    {
        LoadState expect;
        LoadState update;

        do
        {
            expect = this.state.get ();
            if ( expect.isClosed () )
            {
                logger.info ( "Query already closed" );
                return false;
            }

            update = new LoadState ( true, expect.isLoading (), expect.getParameters () );
        } while ( !this.state.compareAndSet ( expect, update ) );

        logger.info ( "Close requested" );
        return true;
    }

    @Override
    public void changeParameters ( final QueryParameters parameters )
    {
        int i = 0;
        LoadState update;
        LoadState expect;

        boolean shouldStart;
        do
        {
            logger.debug ( "Try parameter update - {}", i );
            expect = this.state.get ();

            if ( expect.isClosed () )
            {
                logger.info ( "Query is closed. Bye!" );
                return;
            }

            shouldStart = !expect.isLoading ();

            update = new LoadState ( false, expect.isLoading (), parameters );
            logger.debug ( "Try to apply state: {}", update );
            i++;
        } while ( !this.state.compareAndSet ( expect, update ) );

        if ( shouldStart )
        {
            startLoad ();
        }

        logger.debug ( "State applied: {}", update );

    }

    public void reload ()
    {
        changeParameters ( this.state.get ().getParameters () );
    }

    private void startLoad ()
    {
        logger.info ( "Starting load" );

        this.executor.submit ( new Runnable () {
            @Override
            public void run ()
            {
                performLoad ();
            }
        } );
    }

    protected void performLoad ()
    {
        logger.debug ( "Performing load" );

        LoadState expect;
        LoadState update;

        do
        {
            expect = this.state.get ();
            if ( expect.isLoading () )
            {
                // someone else started loading data ... we can stop
                logger.debug ( "Found loading state. Bye!" );
                return;
            }
            if ( expect.isClosed () )
            {
                // the query got closed .. we can stop
                logger.debug ( "Found closed state. Bye!" );
                return;
            }

            // the new state would be that we are loading, try to set and be the first
            update = new LoadState ( false, true, expect.getParameters () );
        } while ( !this.state.compareAndSet ( expect, update ) );

        // now we are the only running loader
        boolean needStart = false;
        try
        {
            final LoadState current = expect;
            this.buffer.changeParameters ( current.getParameters () );
            this.storage.visit ( current.getParameters (), new ValueVisitor () {

                @Override
                public boolean value ( final double value, final Date date, final boolean error, final boolean manual )
                {
                    QueryImpl.this.buffer.insertData ( value, date, error, manual );
                    final boolean result = shouldContinue ( current.getParameters () );
                    logger.info ( "Requesting early stop" );
                    return result;
                }
            } );
            this.buffer.complete ();

            if ( this.state.get ().isClosed () )
            {
                logger.info ( "Query closed. Bye" );
                // query is close ... quick goodbye
                return;
            }

            if ( hasChanged ( current.getParameters () ) )
            {
                needStart = true;
            }
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to query", e );
            throw new RuntimeException ( "Failed to query", e );
        }
        finally
        {
            endLoading ();
            logger.debug ( "Loading ended" );
        }

        if ( needStart )
        {
            logger.debug ( "Triggering loading restart" );
            // we are done here but need another round for the changed parameters
            startLoad ();
        }
    }

    /**
     * Have the requested parameters changed
     * 
     * @param loadingParameters
     *            the current loading parameters
     * @return <code>true</code> if the provided loading parameters are
     *         different to the current state parameters
     */
    private boolean hasChanged ( final QueryParameters loadingParameters )
    {
        final QueryParameters currentParameters = this.state.get ().getParameters ();
        /*
         * we can compare references here since this is quicker than comparing equality
         * this may cause a double load when the user scroll forward and backward to the same position
         * but is less overhead then comparing equality each time a row was loaded
         */
        return loadingParameters != currentParameters;
    }

    /**
     * Should the current loading continue
     * 
     * @param queryParameters
     * @return
     */
    protected boolean shouldContinue ( final QueryParameters queryParameters )
    {
        final LoadState currentState = this.state.get ();
        if ( currentState.isClosed () )
        {
            return false;
        }

        if ( hasChanged ( queryParameters ) )
        {
            return false;
        }

        return true;
    }

    private void endLoading ()
    {
        LoadState expect;
        LoadState update;

        do
        {
            expect = this.state.get ();
            update = new LoadState ( expect.isClosed (), false, expect.getParameters () );
        } while ( !this.state.compareAndSet ( expect, update ) );
    }

    public boolean isUpdateData ()
    {
        return this.updateData;
    }

    public void updateData ( final double value, final Date timestamp, final boolean error, final boolean manual )
    {
        if ( this.state.get ().isClosed () )
        {
            return;
        }
        this.buffer.updateData ( value, timestamp, error, manual );
    }

    public void dataChanged ( final Date start, final Date end )
    {
        // TODO: implement a partial reload
        reload ();
    }
}
