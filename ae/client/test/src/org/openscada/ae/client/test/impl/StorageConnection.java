/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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
import org.openscada.ae.client.Connection;
import org.openscada.ae.client.test.Activator;
import org.openscada.ae.core.QueryDescription;
import org.openscada.core.client.net.ConnectionBase;
import org.openscada.core.client.net.ConnectionInfo;
import org.openscada.core.client.net.ConnectionStateListener;
import org.openscada.core.client.net.ConnectionBase.State;
import org.openscada.core.client.net.operations.OperationException;
import org.openscada.net.base.LongRunningController;
import org.openscada.net.base.LongRunningOperation;
import org.openscada.net.base.data.Message;


public class StorageConnection extends Observable implements IActionFilter
{
    private static Logger _log = Logger.getLogger ( StorageConnection.class );
    
    private boolean _connectionRequested = false;
    private StorageConnectionInformation _connectionInfo;
    private Connection _connection = null;
    
    private boolean _refreshing = false;
    private Set<StorageQuery> _queries = new HashSet<StorageQuery> (); 
    
    public StorageConnection ( StorageConnectionInformation connectionInfo )
    {
        _connectionInfo = connectionInfo;
        
        ConnectionInfo conInfo = new ConnectionInfo ();
        conInfo.setHostName ( _connectionInfo.getHost () );
        conInfo.setPort ( connectionInfo.getPort () );
        conInfo.setAutoReconnect ( false );
        
        _connection = new Connection ( conInfo );
        _connection.addConnectionStateListener ( new ConnectionStateListener(){

            public void stateChange ( ConnectionBase connection, State state, Throwable error )
            {
                performStateChange ( state, error );
            }
            
            });
        
    }
    
    public void connect ()
    {
        
        _connectionRequested = true;
        setChanged ();
        notifyObservers ();
        
        _log.debug("Initiating connection...");
        
        try
        {
            _connection.connect ();
        }
        catch ( Exception e )
        {
            _log.error ( "Failed to start connection", e );
            org.openscada.ae.client.test.Activator.logError ( 1, "Unable to connect", e );
        }
        _log.debug ( "Connection fired up..." );
    }
    
    public void disconnect ()
    {
        _connectionRequested = false;
        
        setChanged ();
        notifyObservers ();
        
        _connection.disconnect ();
    }
    
    public StorageConnectionInformation getConnectionInformation()
    {
        return _connectionInfo;
    }
    
    private void performStateChange ( Connection.State state, Throwable error )
    {
        switch ( state )
        {
        case BOUND:
            // force refresh
            _queries = null;
            break;
        case CLOSED:
            _queries = new HashSet<StorageQuery> ();
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
        return _connection;
    }

    public boolean isConnectionRequested ()
    {
        return _connectionRequested;
    }
    
    public boolean testAttribute ( Object target, String name, String value )
    {
        if ( name.equals ( "state" ) )
        {
            return _connection.getState ().equals ( State.valueOf ( value ) );
        }
        return false;
    }
    
    synchronized public Set<StorageQuery> getQueries ()
    {
        if ( ( _queries == null ) && !_refreshing )
            refreshQueries ();
        return _queries;
    }

    synchronized public void setQueries ( Set<StorageQuery> queries )
    {
        _queries = queries;
        setChanged ();
        notifyObservers ();
    }
    
    public void refreshQueries ()
    {
        if ( !_connection.getState ().equals ( State.BOUND ) )
            return;
        
        _refreshing = true;
        
        Job job = new Job ( "Refreshing storage" ) {

            @Override
            protected IStatus run ( IProgressMonitor monitor )
            {
                try
                {
                    performRefreshQueries ( monitor );
                }
                catch ( InterruptedException e )
                {
                    setQueries ( new HashSet<StorageQuery> () );
                    return new OperationStatus ( IStatus.ERROR, Activator.PLUGIN_ID, 0, "Failed to refresh queries", e );
                }
                catch ( OperationException e )
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
        
        LongRunningOperation op = _connection.startList ( new LongRunningController.Listener () {

            public void stateChanged ( org.openscada.net.base.LongRunningController.State arg0, Message arg1, Throwable arg2 )
            {
                monitor.setTaskName ( arg0.toString () );
            }} );
        
        try
        {
            op.waitForCompletion ();
            
            Set<StorageQuery> queries = new HashSet<StorageQuery> ();
            for ( QueryDescription queryDescription : _connection.completeList ( op ) )
            {
                queries.add ( new StorageQuery ( this, queryDescription ) );
            }
            setQueries ( queries );
        }
        finally
        {
            monitor.done ();
            _refreshing = false;
        }
        
    }
    
}
