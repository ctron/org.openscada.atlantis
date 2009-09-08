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

package org.openscada.da.server.opc.connection;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jinterop.dcom.core.JISession;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.opc.Hive;
import org.openscada.da.server.opc.browser.OPCBrowserManager;
import org.openscada.da.server.opc.job.Worker;
import org.openscada.da.server.opc.job.impl.ConnectJob;
import org.openscada.da.server.opc.job.impl.GetGroupStateJob;
import org.openscada.da.server.opc.job.impl.ServerStatusJob;
import org.openscada.opc.dcom.da.OPCDATASOURCE;
import org.openscada.opc.dcom.da.OPCGroupState;
import org.openscada.opc.dcom.da.OPCSERVERSTATUS;
import org.openscada.opc.lib.common.ConnectionInformation;

public class OPCController implements Runnable
{
    private static Logger logger = Logger.getLogger ( OPCController.class );

    private static final long LOOP_DELAY_MIN = 50;

    private static final long LOOP_DELAY_MAX = 10 * 1000;

    private ConnectionInformation connectionInformation;

    private volatile boolean running = true;

    private final Worker worker;

    private final OPCModel model;

    private final OPCItemManager itemManager;

    private final OPCIoManager ioManager;

    private final OPCBrowserManager browserManager;

    private final ConnectionSetup configuration;

    private final Collection<OPCStateListener> stateListener = new CopyOnWriteArraySet<OPCStateListener> ();

    private final BlockingQueue<Runnable> jobQueue = new LinkedBlockingQueue<Runnable> ();

    private final GroupState groupState = new GroupState ();

    public OPCController ( final ConnectionSetup config, final Hive hive, final FolderItemFactory itemFactory )
    {
        this.configuration = config;
        this.worker = new Worker ();
        this.model = new OPCModel ();
        this.model.setIgnoreTimestampOnlyChange ( config.isIgnoreTimestampOnlyChange () );
        this.model.setQualityErrorIfLessThen( config.getQualityErrorIfLessThen () );

        switch ( this.configuration.getAccessMethod () )
        {
        case ASYNC20:
            this.ioManager = new OPCAsync2IoManager ( this.worker, this.model, this );
            break;
        default:
            this.ioManager = new OPCSyncIoManager ( this.worker, this.model, this );
            break;
        }

        this.itemManager = new OPCItemManager ( this.worker, this.configuration, this.model, this, hive, itemFactory );
        this.browserManager = new OPCBrowserManager ( this.worker, this.configuration, this.model, hive );
    }

    public void connect ( final ConnectionInformation connectionInformation )
    {
        this.connectionInformation = connectionInformation;
        this.model.setConnectionRequested ( true );
    }

    public void disconnect ()
    {
        this.model.setConnectionRequested ( false );
        this.connectionInformation = null;
    }

    public void submitJob ( final Runnable runnable )
    {
        this.jobQueue.add ( runnable );
    }

    /**
     * The controller main loop
     */
    public void run ()
    {
        while ( this.running )
        {

            try
            {
                final Runnable runnable = this.jobQueue.poll ( this.getModel ().getLoopDelay (), TimeUnit.MILLISECONDS );

                if ( !this.running )
                {
                    // check after sleep
                    return;
                }

                if ( runnable != null )
                {
                    try
                    {
                        logger.debug ( "Running runnable" );
                        runnable.run ();
                    }
                    catch ( final Throwable e )
                    {
                        logger.warn ( "Runnable failed", e );
                        disposeSession ();
                    }
                }
                else
                {
                    logger.debug ( "Running normal queue" );
                    runOnce ();
                }
            }
            catch ( final InterruptedException e )
            {
            }

        }
    }

    protected void setControllerState ( final ControllerState state )
    {
        if ( logger.isDebugEnabled () )
        {
            logger.debug ( String.format ( "Controller state: %s", state ) );
        }
        this.model.setControllerState ( state );
    }

    protected void runOnce ()
    {
        try
        {
            if ( this.model.isConnectionRequested () && ! ( this.model.isConnected () || this.model.isConnecting () ) && this.model.mayConnect () )
            {
                setControllerState ( ControllerState.CONNECTING );
                if ( performConnect () )
                {
                    this.itemManager.handleConnected ();
                    this.ioManager.handleConnected ();
                    fireConnected ();
                }
            }
            else if ( !this.model.isConnectionRequested () && this.model.isConnected () )
            {
                setControllerState ( ControllerState.DISCONNECTING );
                performDisconnect ();
            }

            if ( this.model.isConnected () )
            {
                setControllerState ( ControllerState.READING_STATUS );
                updateStatus ();
                setControllerState ( ControllerState.GET_GROUP_STATUS );
                updateGroupStatus ();

                final OPCIoContext ctx = this.ioManager.prepareProcessing ();
                this.ioManager.performProcessing ( ctx, OPCDATASOURCE.OPC_DS_CACHE );

                setControllerState ( ControllerState.BROWSING );
                this.browserManager.performBrowse ();
            }

            setControllerState ( ControllerState.IDLE );
        }
        catch ( final Throwable e )
        {
            logger.error ( "Failed to process", e );
            disposeSession ();
        }
    }

    /**
     * Request the status from the OPC server
     */
    private void updateStatus ()
    {
        final ServerStatusJob job = new ServerStatusJob ( this.model.getStatusJobTimeout (), this.model );

        try
        {
            setServerState ( this.worker.execute ( job, job ) );
        }
        catch ( final InvocationTargetException e )
        {
            disposeSession ();
        }
    }

    private void updateGroupStatus () throws InvocationTargetException
    {
        final GetGroupStateJob job = new GetGroupStateJob ( this.model.getStatusJobTimeout (), this.model );
        setGroupState ( this.worker.execute ( job, job ) );
    }

    private void setGroupState ( final OPCGroupState state )
    {
        this.groupState.update ( state );

    }

    protected void setServerState ( final OPCSERVERSTATUS state )
    {
        this.model.setServerState ( state );
    }

    private boolean performConnect ()
    {
        this.model.setLastConnectNow ();
        this.model.setConnecting ( true );
        this.model.setConnectionState ( ConnectionState.CONNECTING );

        final ConnectJob job = new ConnectJob ( this.model.getConnectJobTimeout (), this.connectionInformation, this.model.getGlobalTimeout (), this.model.getUpdateRate () );
        final OPCModel model = this.model;

        try
        {
            this.worker.execute ( job, new Runnable () {

                public void run ()
                {
                    model.setSession ( job.getSession () );
                    model.setServer ( job.getServer () );
                    model.setCommon ( job.getCommon () );
                    model.setGroup ( job.getGroup () );
                    model.setSyncIo ( job.getSyncIo () );
                    model.setItemMgt ( job.getItemMgt () );
                    model.setAsyncIo2 ( job.getAsyncIo2 () );

                    model.setConnectionState ( ConnectionState.CONNECTED );
                }
            } );
        }
        catch ( final InvocationTargetException e )
        {
            logger.info ( "Failed to connect", e );
            this.model.setLastConnectionError ( e.getCause () );
            model.setConnectionState ( ConnectionState.DISCONNECTED );
            disposeSession ( job.getSession () );
            return false;
        }
        finally
        {
            model.setConnecting ( false );
        }
        return true;
    }

    private void performDisconnect ()
    {
        disposeSession ();
    }

    protected void disposeSession ( final JISession session )
    {
        logger.info ( "Destroying DCOM session..." );
        final Thread destructor = new Thread ( new Runnable () {

            public void run ()
            {
                OPCController.this.model.addDisposerRunning ( Thread.currentThread () );
                final long ts = System.currentTimeMillis ();
                try
                {
                    logger.debug ( "Starting destruction of DCOM session" );
                    JISession.destroySession ( session );
                    logger.info ( "Destructed DCOM session" );
                }
                catch ( final Throwable e )
                {
                    logger.warn ( "Failed to destruct DCOM session", e );
                }
                finally
                {
                    logger.info ( String.format ( "Session destruction took %s ms", System.currentTimeMillis () - ts ) );
                    OPCController.this.model.removeDisposerRunning ( Thread.currentThread () );
                }
            }
        } );

        destructor.setName ( "OPCSessionDestructor/" + this.configuration.getDeviceTag () );
        destructor.setDaemon ( true );
        destructor.start ();
        logger.info ( "Destroying DCOM session... forked" );
    }

    protected void disposeSession ()
    {
        if ( this.model.getSession () == null )
        {
            return;
        }

        this.model.setConnectionState ( ConnectionState.DISCONNECTING );

        disposeSession ( this.model.getSession () );

        this.itemManager.handleDisconnected ();
        this.ioManager.handleDisconnected ();
        fireDisconnected ();

        this.model.setServerState ( null );
        this.model.setConnecting ( false );
        this.model.setServer ( null );
        this.model.setSession ( null );
        this.model.setGroup ( null );
        this.model.setItemMgt ( null );
        this.model.setSyncIo ( null );
        this.model.setAsyncIo2 ( null );
        this.model.setCommon ( null );

        this.model.setConnectionState ( ConnectionState.DISCONNECTED );
        setGroupState ( null );
    }

    public void shutdown ()
    {
        this.itemManager.shutdown ();
        this.ioManager.shutdown ();
        this.running = false;
    }

    public OPCModel getModel ()
    {
        return this.model;
    }

    public OPCItemManager getItemManager ()
    {
        return this.itemManager;
    }

    public OPCIoManager getIoManager ()
    {
        return this.ioManager;
    }

    public OPCBrowserManager getBrowserManager ()
    {
        return this.browserManager;
    }

    public void setLoopDelay ( long loopDelay )
    {
        if ( loopDelay < LOOP_DELAY_MIN )
        {
            loopDelay = LOOP_DELAY_MIN;
        }
        if ( loopDelay > LOOP_DELAY_MAX )
        {
            loopDelay = LOOP_DELAY_MAX;
        }
        this.model.setLoopDelay ( loopDelay );
    }

    public void addStateListener ( final OPCStateListener stateListener )
    {
        this.stateListener.add ( stateListener );
    }

    public void removeStateListener ( final OPCStateListener stateListener )
    {
        this.stateListener.remove ( stateListener );
    }

    protected void fireConnected ()
    {
        for ( final OPCStateListener listener : this.stateListener )
        {
            listener.connectionEstablished ();
        }
    }

    protected void fireDisconnected ()
    {
        for ( final OPCStateListener listener : this.stateListener )
        {
            listener.connectionLost ();
        }
    }

    public GroupState getGroupState ()
    {
        return this.groupState;
    }
}
