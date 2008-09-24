/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.opc2.connection;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;
import org.jinterop.dcom.core.JISession;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.opc2.Hive;
import org.openscada.da.server.opc2.browser.OPCBrowserManager;
import org.openscada.da.server.opc2.job.Worker;
import org.openscada.da.server.opc2.job.impl.ConnectJob;
import org.openscada.da.server.opc2.job.impl.ServerStatusJob;
import org.openscada.opc.dcom.da.OPCDATASOURCE;
import org.openscada.opc.dcom.da.OPCSERVERSTATUS;
import org.openscada.opc.lib.common.ConnectionInformation;

public class OPCController implements Runnable
{
    private static final long LOOP_DELAY_MIN = 50;

    private static final long LOOP_DELAY_MAX = 10 * 1000;

    private ConnectionInformation connectionInformation;

    private static Logger logger = Logger.getLogger ( OPCController.class );

    private boolean running = true;

    private Worker worker;

    private OPCModel model;

    private OPCItemManager itemManager;

    private OPCIoManager ioManager;

    private OPCBrowserManager browserManager;

    private ConnectionSetup configuration;
    
    private Collection<OPCStateListener> stateListener = new CopyOnWriteArraySet<OPCStateListener> ();

    public OPCController ( ConnectionSetup config, Hive hive, FolderItemFactory itemFactory )
    {
        this.configuration = config;
        worker = new Worker ();
        model = new OPCModel ();
        model.setIgnoreTimestampOnlyChange ( config.isIgnoreTimestampOnlyChange () );

        ioManager = new OPCIoManager ( worker, model, this );
        itemManager = new OPCItemManager ( worker, configuration, model, this, hive, itemFactory );
        browserManager = new OPCBrowserManager ( worker, configuration, model, hive );
    }

    public void connect ( ConnectionInformation connectionInformation )
    {
        this.connectionInformation = connectionInformation;
        this.model.setConnectionRequested ( true );
    }

    public void disconnect ()
    {
        this.model.setConnectionRequested ( false );
        this.connectionInformation = null;
    }

    public void run ()
    {
        while ( running )
        {
            try
            {
                Thread.sleep ( this.getModel ().getLoopDelay () );
            }
            catch ( InterruptedException e )
            {
                logger.warn ( "Sleep failed", e );
            }

            if ( !running )
            {
                // check after sleep
                return;
            }

            runOnce ();
        }
    }

    protected void setControllerState ( ControllerState state )
    {
        this.model.setControllerState ( state );
    }

    protected void runOnce ()
    {
        try
        {
            if ( this.model.isConnectionRequested () && ! ( this.model.isConnected () || this.model.isConnecting () ) && model.mayConnect () )
            {
                setControllerState ( ControllerState.CONNECTING );
                if ( performConnect () )
                {
                    itemManager.handleConnected ();
                    ioManager.handleConnected ();
                    fireConnected ();
                }
            }
            else if ( !this.model.isConnectionRequested () && this.model.isConnected () )
            {
                setControllerState ( ControllerState.DISCONNECTING );
                performDisconnect ();
            }

            if ( model.isConnected () )
            {
                setControllerState ( ControllerState.READING_STATUS );
                updateStatus ();

                OPCIoContext ctx = ioManager.prepareProcessing ();
                ioManager.performProcessing ( ctx, OPCDATASOURCE.OPC_DS_CACHE );

                /*
                ioManager.processRequests ();

                setControllerState ( ControllerState.ACTIVATING );
                ioManager.processActivations ();

                setControllerState ( ControllerState.WRITING );
                ioManager.processWriteRequests ();

                setControllerState ( ControllerState.READING );
                ioManager.read ( OPCDATASOURCE.OPC_DS_CACHE );
                */

                setControllerState ( ControllerState.BROWSING );
                browserManager.performBrowse ();
            }

            setControllerState ( ControllerState.IDLE );
        }
        catch ( Throwable e )
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
            setServerState ( worker.execute ( job, job ) );
        }
        catch ( InvocationTargetException e )
        {
            disposeSession ();
        }
    }

    protected void setServerState ( OPCSERVERSTATUS state )
    {
        model.setServerState ( state );
    }

    private boolean performConnect ()
    {
        model.setLastConnectNow ();
        model.setConnecting ( true );
        model.setConnectionState ( ConnectionState.CONNECTING );

        final ConnectJob job = new ConnectJob ( this.model.getConnectJobTimeout (), this.connectionInformation, model.getGlobalTimeout (), model.getUpdateRate () );
        final OPCModel model = this.model;

        try
        {
            worker.execute ( job, new Runnable () {

                public void run ()
                {
                    model.setSession ( job.getSession () );
                    model.setServer ( job.getServer () );
                    model.setCommon ( job.getCommon () );
                    model.setGroup ( job.getGroup () );
                    model.setSyncIo ( job.getSyncIo () );
                    model.setItemMgt ( job.getItemMgt () );

                    model.setConnectionState ( ConnectionState.CONNECTED );
                }
            } );
        }
        catch ( InvocationTargetException e )
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
        Thread destructor = new Thread ( new Runnable () {

            public void run ()
            {
                model.addDisposerRunning ( Thread.currentThread () );
                long ts = System.currentTimeMillis ();
                try
                {
                    logger.debug ( "Starting destruction of DCOM session" );
                    JISession.destroySession ( session );
                    logger.info ( "Destructed DCOM session" );
                }
                catch ( Throwable e )
                {
                    logger.warn ( "Failed to destruct DCOM session", e );
                }
                finally
                {
                    logger.info ( String.format ( "Session destruction took %s ms", System.currentTimeMillis () - ts ) );
                    model.removeDisposerRunning ( Thread.currentThread () );
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
        if ( model.getSession () == null )
        {
            return;
        }

        this.model.setConnectionState ( ConnectionState.DISCONNECTING );

        disposeSession ( model.getSession () );

        this.itemManager.handleDisconnected ();
        this.ioManager.handleDisconnected ();
        fireDisconnected ();

        model.setServerState ( null );
        model.setConnecting ( false );
        model.setServer ( null );
        model.setSession ( null );
        model.setGroup ( null );
        model.setItemMgt ( null );
        model.setSyncIo ( null );
        model.setCommon ( null );

        this.model.setConnectionState ( ConnectionState.DISCONNECTED );
    }

    public void shutdown ()
    {
        this.itemManager.shutdown ();
        this.ioManager.shutdown ();
        this.running = false;
    }

    public OPCModel getModel ()
    {
        return model;
    }

    public OPCItemManager getItemManager ()
    {
        return itemManager;
    }

    public OPCIoManager getIoManager ()
    {
        return ioManager;
    }

    public OPCBrowserManager getBrowserManager ()
    {
        return browserManager;
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
    
    public void addStateListener ( OPCStateListener stateListener )
    {
        this.stateListener.add ( stateListener );
    }
    
    public void removeStateListener ( OPCStateListener stateListener )
    {
        this.stateListener.remove ( stateListener );
    }
    
    protected void fireConnected ()
    {
        for ( OPCStateListener listener : this.stateListener )
        {
            listener.connectionEstablished ();
        }
    }
    
    protected void fireDisconnected ()
    {
        for ( OPCStateListener listener : this.stateListener )
        {
            listener.connectionLost ();
        }
    }
}
