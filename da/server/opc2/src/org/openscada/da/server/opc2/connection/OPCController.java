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

import org.apache.log4j.Logger;
import org.jinterop.dcom.core.JISession;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.opc2.Hive;
import org.openscada.da.server.opc2.job.Worker;
import org.openscada.da.server.opc2.job.impl.ConnectJob;
import org.openscada.da.server.opc2.job.impl.ServerStatusJob;
import org.openscada.opc.dcom.da.OPCDATASOURCE;
import org.openscada.opc.dcom.da.OPCSERVERSTATUS;
import org.openscada.opc.lib.common.ConnectionInformation;

public class OPCController implements Runnable
{
    private ConnectionInformation connectionInformation;

    private static Logger logger = Logger.getLogger ( OPCController.class );

    private boolean running = true;

    private Worker worker;

    private OPCModel model;

    private OPCItemManager itemManager;

    private OPCConfiguration configuration;

    public OPCController ( OPCConfiguration config, Hive hive, FolderCommon connectionFolder )
    {
        this.configuration = config;
        worker = new Worker ();
        model = new OPCModel ();

        itemManager = new OPCItemManager ( worker, configuration, model, hive, connectionFolder );
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
                Thread.sleep ( 250 );
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

    protected void runOnce ()
    {
        try
        {
            if ( this.model.isConnectionRequested () && ! ( this.model.isConnected () || this.model.isConnecting () )
                    && model.mayConnect () )
            {
                if ( performConnect () )
                {
                    this.itemManager.handleConnected ();
                }
            }
            else if ( !this.model.isConnectionRequested () && this.model.isConnected () )
            {
                performDisconnect ();
            }

            if ( model.isConnected () )
            {
                updateStatus ();
                itemManager.processRequests ();
                itemManager.processActivations ();
                itemManager.processWriteRequests ();
                itemManager.read ( OPCDATASOURCE.OPC_DS_CACHE );
            }
        }
        catch ( Throwable e )
        {
            logger.error ( "Failed to process", e );
            disposeSession ();
        }
    }

    private void updateStatus ()
    {
        final ServerStatusJob job = new ServerStatusJob ( this.model );

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

        final ConnectJob job = new ConnectJob ( this.connectionInformation, 5000 );
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
                }
            } );
        }
        catch ( InvocationTargetException e )
        {
            logger.info ( "Failed to connect", e );
            this.model.setLastConnectionError ( e.getCause () );
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
                }
            }
        }, "OPCSessionDestructor" );
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

        disposeSession ( model.getSession () );

        this.itemManager.handleDisconnected ();

        model.setServerState ( null );
        model.setConnecting ( false );
        model.setServer ( null );
        model.setSession ( null );
        model.setGroup ( null );
        model.setItemMgt ( null );
        model.setSyncIo ( null );
        model.setCommon ( null );
    }

    public void shutdown ()
    {
        this.itemManager.shutdown ();
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
}
