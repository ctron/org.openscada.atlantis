/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.ae.client.test.impl;

import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IActionFilter;
import org.openscada.ae.client.net.Connection;
import org.openscada.ae.client.test.Activator;
import org.openscada.ae.core.QueryDescription;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.OperationException;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningOperation;
import org.openscada.utils.exec.LongRunningState;

public class StorageConnection extends Observable implements IActionFilter
{
    private static Logger _log = Logger.getLogger ( StorageConnection.class );

    private boolean _connectionRequested = false;

    private final StorageConnectionInformation _connectionInfo;

    private Connection _connection = null;

    private boolean refreshing = false;

    private Set<StorageQuery> _queries = new HashSet<StorageQuery> ();

    public StorageConnection ( final StorageConnectionInformation connectionInfo )
    {
        this._connectionInfo = connectionInfo;

        this._connection = new org.openscada.ae.client.net.Connection ( ConnectionInformation.fromURI ( String.format ( "ae:net://%s:%s", this._connectionInfo.getHost (), this._connectionInfo.getPort () ) ) );
        this._connection.addConnectionStateListener ( new ConnectionStateListener () {

            public void stateChange ( final org.openscada.core.client.Connection connection, final ConnectionState state, final Throwable error )
            {
                performStateChange ( state, error );
            }

        } );
    }

    public void connect ()
    {
        this._connectionRequested = true;
        setChanged ();
        notifyObservers ();

        _log.debug ( "Initiating connection..." );

        try
        {
            this._connection.connect ();
        }
        catch ( final Exception e )
        {
            _log.error ( "Failed to start connection", e );
            org.openscada.ae.client.test.Activator.logError ( 1, "Unable to connect", e );
        }
        _log.debug ( "Connection fired up..." );
    }

    public void disconnect ()
    {
        this._connectionRequested = false;

        setChanged ();
        notifyObservers ();

        this._connection.disconnect ();
    }

    public StorageConnectionInformation getConnectionInformation ()
    {
        return this._connectionInfo;
    }

    private void performStateChange ( final ConnectionState state, final Throwable error )
    {
        switch ( state )
        {
        case BOUND:
            // force refresh
            this._queries = null;
            break;
        case CLOSED:
            this._queries = new HashSet<StorageQuery> ();
            break;
        default:
            break;
        }

        setChanged ();
        notifyObservers ();

        if ( error != null )
        {
            org.openscada.ae.client.test.Activator.getDefault ().notifyError ( "Connection failed", error );
        }
    }

    public Connection getConnection ()
    {
        return this._connection;
    }

    public boolean isConnectionRequested ()
    {
        return this._connectionRequested;
    }

    public boolean testAttribute ( final Object target, final String name, final String value )
    {
        if ( name.equals ( "state" ) )
        {
            return this._connection.getState ().equals ( ConnectionState.valueOf ( value ) );
        }
        return false;
    }

    synchronized public Set<StorageQuery> getQueries ()
    {
        if ( this._queries == null && !this.refreshing )
        {
            refreshQueries ();
        }
        return this._queries;
    }

    synchronized public void setQueries ( final Set<StorageQuery> queries )
    {
        this._queries = queries;
        setChanged ();
        notifyObservers ();
    }

    public void refreshQueries ()
    {
        if ( !this._connection.getState ().equals ( ConnectionState.BOUND ) )
        {
            return;
        }

        this.refreshing = true;

        final Job job = new Job ( "Refreshing storage" ) {

            @Override
            protected IStatus run ( final IProgressMonitor monitor )
            {
                try
                {
                    performRefreshQueries ( monitor );
                }
                catch ( final InterruptedException e )
                {
                    setQueries ( new HashSet<StorageQuery> () );
                    return new OperationStatus ( IStatus.ERROR, Activator.PLUGIN_ID, 0, "Failed to refresh queries", e );
                }
                catch ( final OperationException e )
                {
                    setQueries ( new HashSet<StorageQuery> () );
                    return new OperationStatus ( IStatus.ERROR, Activator.PLUGIN_ID, 1, "Failed to refresh queries", e );
                }
                return Status.OK_STATUS;
            }
        };
        job.setUser ( true );
        job.schedule ();
    }

    private void performRefreshQueries ( final IProgressMonitor monitor ) throws InterruptedException, OperationException
    {
        setQueries ( null );

        final LongRunningOperation op = this._connection.startList ( new LongRunningListener () {

            public void stateChanged ( final LongRunningOperation operation, final LongRunningState state, final Throwable error )
            {
                monitor.setTaskName ( state.toString () );
            }
        } );

        try
        {
            op.waitForCompletion ();

            final Set<StorageQuery> queries = new HashSet<StorageQuery> ();
            for ( final QueryDescription queryDescription : this._connection.completeList ( op ) )
            {
                queries.add ( new StorageQuery ( this, queryDescription ) );
            }
            setQueries ( queries );
        }
        finally
        {
            monitor.done ();
            this.refreshing = false;
        }

    }

}
