/*******************************************************************************
 * Copyright (c) 2015 IBH SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBH SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.openscada.da.server.opc.xmlda;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.da.server.common.AttributeMode;
import org.eclipse.scada.da.server.common.DataItem;
import org.eclipse.scada.da.server.common.chain.item.ChainCreator;
import org.eclipse.scada.da.server.common.exporter.StaticObjectExporter;
import org.eclipse.scada.da.server.common.impl.HiveCommon;
import org.eclipse.scada.da.server.common.item.factory.DefaultChainItemFactory;
import org.eclipse.scada.da.server.common.item.factory.FolderItemFactory;
import org.eclipse.scada.utils.ExceptionHelper;
import org.eclipse.scada.utils.concurrent.FutureListener;
import org.eclipse.scada.utils.concurrent.NotifyFuture;
import org.eclipse.scada.utils.concurrent.ScheduledExportedExecutorService;
import org.openscada.opc.xmlda.Connection;
import org.openscada.opc.xmlda.Poller;
import org.openscada.opc.xmlda.SubscriptionListener;
import org.openscada.opc.xmlda.SubscriptionState;
import org.openscada.opc.xmlda.requests.GetStatusRequest;
import org.openscada.opc.xmlda.requests.GetStatusResponse;
import org.openscada.opc.xmlda.requests.ItemValue;
import org.openscada.opc.xmlda.requests.Quality;
import org.openscada.opc.xmlda.requests.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerConnection implements SubscriptionListener
{
    public static final Logger logger = LoggerFactory.getLogger ( ServerConnection.class );

    public static final String DATA_DELIM = System.getProperty ( "org.openscada.da.server.opc.xmlda.dataDelimiter", "!" );

    private int waitTime;

    private Integer samplingRate;

    private final String id;

    private Connection connection;

    private final ScheduledExportedExecutorService executor;

    private StaticObjectExporter<ServerStateInformation> serverStateExporter;

    private FolderItemFactory stateItemFactory;

    private final FolderCommon connectionFolder;

    private final HiveCommon hive;

    private final FolderCommon rootFolder;

    private final Map<String, RemoteDataItem> items = new HashMap<> ();

    private Poller poller;

    public ServerConnection ( final String id, ServerConfiguration configuration, final HiveCommon hive, final FolderCommon rootFolder )
    {
        this.id = id;

        this.connection = new Connection ( configuration.getWsdlUrl (), configuration.getServerUrl (), configuration.getServiceName (), configuration.getLocalPart (), configuration.getConnectTimeout (), configuration.getRequestTimeout () );

        this.executor = new ScheduledExportedExecutorService ( makeBeanName ( "XMLDA/" + this.connection.toString () ) );

        this.hive = hive;
        this.rootFolder = rootFolder;
        this.connectionFolder = new FolderCommon ();

        // setup up browsing

        final Map<String, Variant> attributes = new HashMap<> ();
        attributes.put ( "description", Variant.valueOf ( "The root node of the server namespace" ) );
        this.connectionFolder.add ( "tree", new BrowserFolder ( this.connection, makeDataId ( null ), null, null ), attributes );
    }

    private static String makeBeanName ( final String string )
    {
        return string.replaceAll ( "[?*.:]+", "_" );
    }

    public void start ()
    {
        // start poller

        this.poller = this.connection.createPoller ( this, this.waitTime, this.samplingRate );

        // attach connection folder

        final Map<String, Variant> attributes = new HashMap<> ( 1 );
        attributes.put ( "description", Variant.valueOf ( "Folder for connection: " + this.connection ) );

        this.rootFolder.add ( this.id, this.connectionFolder, attributes );

        // create root factory

        this.stateItemFactory = new DefaultChainItemFactory ( this.hive, this.connectionFolder, makeId ( "state" ), "state" );

        // create status object and items

        this.serverStateExporter = new StaticObjectExporter<ServerStateInformation> ( this.stateItemFactory.createSubFolderFactory ( "server" ), ServerStateInformation.class, true, false, null );

        // start nagging server with status requests

        triggerStatus ();
    }

    /**
     * This disposes the server instance, it cannot be re-restarted
     */
    public void dispose ()
    {
        this.rootFolder.remove ( this.connectionFolder );

        if ( this.poller != null )
        {
            this.poller.dispose ();
            this.poller = null;
        }

        if ( this.serverStateExporter != null )
        {
            this.serverStateExporter.dispose ();
            this.serverStateExporter = null;
        }

        if ( this.stateItemFactory != null )
        {
            this.stateItemFactory.dispose ();
            this.stateItemFactory = null;
        }

        for ( final RemoteDataItem item : this.items.values () )
        {
            this.hive.unregisterItem ( item );
        }
        this.items.clear ();

        try
        {
            this.connection.close ();
            this.connection = null;
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to dispose connection", e );
        }

        this.executor.shutdown ();
    }

    private String makeId ( final String localId )
    {
        if ( localId == null )
        {
            return this.id;
        }

        return this.id + "." + localId;
    }

    private String makeDataId ( final String remoteId )
    {
        if ( remoteId == null )
        {
            return this.id + ".data" + DATA_DELIM;
        }
        else
        {
            return this.id + ".data" + DATA_DELIM + remoteId;
        }
    }

    private void triggerStatus ()
    {
        final Connection connection = this.connection;
        if ( connection == null )
        {
            return;
        }

        final NotifyFuture<GetStatusResponse> future = connection.scheduleTask ( new GetStatusRequest () );
        future.addListener ( new FutureListener<GetStatusResponse> () {

            @Override
            public void complete ( final Future<GetStatusResponse> future )
            {
                handleStateResult ( future );
            }
        } );
    }

    protected void handleStateResult ( final Future<GetStatusResponse> future )
    {
        try
        {
            final GetStatusResponse result = future.get ();

            final ServerStateInformation state = new ServerStateInformation ();
            state.setState ( result.getServerState () );
            state.setProductVersion ( result.getProductVersion () );
            state.setVendorInformation ( result.getVendorInformation () );
            state.setStartTime ( makeTime ( result.getStartTime () ) );
            state.setInformation ( result.getStatusInformation () );

            final Map<String, Variant> attributes = new HashMap<> ( 0 );
            this.serverStateExporter.setTarget ( state, attributes );
        }
        catch ( InterruptedException | ExecutionException e )
        {
            logger.info ( "Failed to fetch status", e );
            final Map<String, Variant> attributes = new HashMap<> ( 1 );
            attributes.put ( "connection.error", Variant.TRUE );
            attributes.put ( "connection.error.message", Variant.valueOf ( ExceptionHelper.getMessage ( e ) ) );
            this.serverStateExporter.setTarget ( new ServerStateInformation (), attributes );
        }
        finally
        {
            this.executor.schedule ( new Runnable () {
                @Override
                public void run ()
                {
                    triggerStatus ();
                }
            }, 5_000, TimeUnit.MILLISECONDS );
        }
    }

    private String makeTime ( final Calendar time )
    {
        if ( time == null )
        {
            return null;
        }

        final DateFormat format = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss.SSS z" );
        format.setTimeZone ( time.getTimeZone () );
        return format.format ( time.getTime () );
    }

    public synchronized DataItem getRemoteDataItem ( final String remoteId )
    {
        final String clientHandle = makeDataId ( remoteId );

        RemoteDataItem item = this.items.get ( clientHandle );
        if ( item != null )
        {
            return item;
        }

        item = new RemoteDataItem ( clientHandle, this.hive.getOperationService (), this.connection, this.poller, remoteId, null );
        ChainCreator.applyDefaultInputChain ( item );
        this.items.put ( clientHandle, item );

        this.hive.registerItem ( item );

        return item;
    }

    @Override
    public synchronized void dataChange ( final Map<String, ItemValue> values )
    {
        logger.debug ( "Data changed: {}", values.size () );

        for ( final Map.Entry<String, ItemValue> entry : values.entrySet () )
        {
            setItemValue ( entry.getKey (), entry.getValue () );
        }
    }

    protected synchronized void setItemValue ( final String clientHandle, final ItemValue value )
    {
        final RemoteDataItem item = this.items.get ( clientHandle );
        if ( item == null )
        {
            logger.debug ( "Item {} not found", clientHandle );
            return;
        }

        final Map<String, Variant> attributes = new HashMap<> ();

        if ( value.getTimestamp () != null )
        {
            attributes.put ( "timestamp", Variant.valueOf ( value.getTimestamp ().getTimeInMillis () ) );
        }

        final State state = value.getState ();
        if ( state.getQuality () == null || !state.isGood () )
        {
            attributes.put ( "quality.error", Variant.TRUE );
        }

        if ( state.getQuality () == Quality.GOOD_LOCAL_OVERRIDE )
        {
            attributes.put ( "opc.manual", Variant.TRUE );
        }

        final Variant variant = convert ( value.getValue () );

        logger.debug ( "Set item value: {} = {} ({})", clientHandle, variant, value.getValue () );

        item.updateData ( variant, attributes, AttributeMode.SET );
    }

    private Variant convert ( final Object value )
    {
        if ( value instanceof Calendar )
        {
            return Variant.valueOf ( ( (Calendar)value ).getTimeInMillis () );
        }
        return Variant.valueOf ( value );
    }

    @Override
    public synchronized void stateChange ( final SubscriptionState state )
    {
        if ( state != SubscriptionState.ACTIVE )
        {
            final Map<String, Variant> attributes = new HashMap<> ( 3 );

            attributes.put ( "subscription.error", Variant.TRUE );
            attributes.put ( "subscription.state", Variant.valueOf ( state ) );
            attributes.put ( "timestamp", Variant.valueOf ( System.currentTimeMillis () ) );

            for ( final RemoteDataItem item : this.items.values () )
            {
                item.updateData ( Variant.NULL, attributes, AttributeMode.SET );
            }
        }
    }
}
