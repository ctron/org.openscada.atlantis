/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.dave;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.openscada.core.Variant;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.osgi.factory.DataItemFactory;
import org.openscada.protocols.dave.DaveConnectionEstablishedMessage;
import org.openscada.protocols.dave.DaveFilter;
import org.openscada.protocols.dave.DaveGenericMessage;
import org.openscada.protocols.dave.DaveMessage;
import org.openscada.protocols.dave.DaveWriteRequest;
import org.openscada.protocols.iso8073.COTPFilter;
import org.openscada.protocols.tkpt.TPKTFilter;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaveDevice implements IoHandler
{

    private final static Logger logger = LoggerFactory.getLogger ( DaveDevice.class );

    private final ScheduledExecutorService executor;

    private NioSocketConnector connector;

    private IoSession session;

    private String host;

    private Short port;

    private Integer rack;

    private Byte slot;

    private final BundleContext context;

    private final String id;

    private final DataItemInputChained stateItem;

    private final DataItemInputChained configItem;

    private final DaveBlockConfigurator configurator;

    private final DaveJobManager jobManager;

    private final DataItemInputChained connectionItem;

    private final DataItemFactory itemFactory;

    private int readTimeout;

    private String name;

    private boolean disposed;

    private ConnectionState state = ConnectionState.DISCONNECTED;

    private static enum ConnectionState
    {
        CONNECTING,
        CONNECTED,
        DISCONNECTED;
    }

    public DaveDevice ( final BundleContext context, final String id, final Map<String, String> properties ) throws Exception
    {
        this.id = id;
        this.context = context;
        this.executor = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( "DaveDevice/" + id ) );

        this.jobManager = new DaveJobManager ( this );
        this.configurator = new DaveBlockConfigurator ( this, this.context );

        this.itemFactory = new DataItemFactory ( context, this.executor, getItemId ( null ) );

        final Map<String, Variant> props = new HashMap<String, Variant> ();

        this.stateItem = this.itemFactory.createInput ( "state", props );
        this.connectionItem = this.itemFactory.createInput ( "connection", props );
        this.configItem = this.itemFactory.createInput ( "config", props );

        update ( properties );
    }

    public String getItemId ( final String localId )
    {
        if ( localId != null )
        {
            return "dave." + this.id + "." + localId;
        }
        else
        {
            return "dave." + this.id;
        }
    }

    public String getVarItemId ( final String localId )
    {
        final String name = this.name;
        if ( name == null )
        {
            this.name = this.id;
        }

        if ( localId != null )
        {
            return "dave." + name + "." + localId;
        }
        else
        {
            return "dave." + name;
        }
    }

    public void dispose ()
    {
        this.disposed = true;

        this.configurator.dispose ();

        this.itemFactory.dispose ();

        this.jobManager.dispose ();

        disconnect ();

        if ( this.connector != null )
        {
            this.connector.dispose ();
            this.connector = null;
        }

        this.executor.shutdown ();
    }

    public void update ( final Map<String, String> properties ) throws Exception
    {
        this.host = properties.get ( "host" );
        this.port = Short.valueOf ( properties.get ( "port" ) );
        this.rack = Integer.valueOf ( properties.get ( "rack" ) );
        this.slot = Byte.valueOf ( properties.get ( "slot" ) );
        this.readTimeout = Integer.valueOf ( properties.get ( "readTimeout" ) );
        this.name = properties.get ( "name" );

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "host", new Variant ( this.host ) );
        attributes.put ( "port", new Variant ( this.port ) );
        attributes.put ( "rack", new Variant ( this.rack ) );
        attributes.put ( "slot", new Variant ( this.slot ) );
        attributes.put ( "name", new Variant ( this.name ) );
        this.configItem.updateData ( Variant.TRUE, attributes, AttributeMode.SET );

        synchronized ( this )
        {
            disconnect ();
            connect ();
        }
    }

    private synchronized void connect ()
    {
        if ( this.state != ConnectionState.DISCONNECTED )
        {
            logger.warn ( "Tried to connect in state: {}", this.state );
            return;
        }

        if ( this.session != null )
        {
            logger.error ( "We already have a session: {}", this.session );
            logger.error ( "Connect error", new IllegalStateException ().fillInStackTrace () );
            return;
        }

        if ( this.connector == null )
        {
            this.connector = new NioSocketConnector ();

            this.connector.setHandler ( this );

            // this.connector.getFilterChain ().addLast ( "logger", new LoggingFilter ( this.getClass ().getName () ) );
            this.connector.getFilterChain ().addLast ( "tpkt", new TPKTFilter ( 3 ) );
            this.connector.getFilterChain ().addLast ( "cotp", new COTPFilter ( this.rack, this.slot ) );
            this.connector.getFilterChain ().addLast ( "dave", new DaveFilter () );
            this.connector.setConnectTimeoutMillis ( this.readTimeout );
        }

        setState ( ConnectionState.CONNECTING );

        final ConnectFuture future = this.connector.connect ( new InetSocketAddress ( this.host, this.port ) );
        future.addListener ( new IoFutureListener<IoFuture> () {

            public void operationComplete ( final IoFuture future )
            {
                try
                {
                    // set new session
                    setSession ( future.getSession () );
                }
                catch ( final Exception e )
                {
                    // handle case of re-connect failure
                    disconnected ();
                }
            }
        } );
    }

    private void setState ( final ConnectionState state )
    {
        this.state = state;
        this.stateItem.updateData ( new Variant ( state.toString () ), null, null );
        this.connectionItem.updateData ( Variant.valueOf ( state == ConnectionState.CONNECTED ), null, null );
    }

    protected synchronized void setSession ( final IoSession session )
    {
        if ( this.session != null && session != null )
        {
            logger.error ( "We already have a session set!" );
        }

        if ( session != null )
        {
            setState ( ConnectionState.CONNECTED );
        }
        else
        {
            logger.info ( "Disconnected" );
            setState ( ConnectionState.DISCONNECTED );
            this.jobManager.setSession ( null );
        }
        this.session = session;
    }

    private synchronized void disconnected ()
    {
        if ( this.disposed )
        {
            return;
        }

        setSession ( null );
        this.executor.schedule ( new Runnable () {

            public void run ()
            {
                connect ();
            }
        }, 1000, TimeUnit.MILLISECONDS );
    }

    protected synchronized void disconnect ()
    {
        if ( this.session != null )
        {
            if ( !this.session.isClosing () )
            {
                logger.info ( "Close session: {}", this.session );
                this.session.close ( true );
            }
            else
            {
                logger.warn ( "Session already closing: {}", this.session );
            }
            // session will be set to null using #sessionClosed()
        }
        else
        {
            logger.warn ( "Disconnected without session" );
        }
    }

    public synchronized void exceptionCaught ( final IoSession session, final Throwable error ) throws Exception
    {
        logger.warn ( "Exception caught", error );

        if ( ! ( error instanceof WrongSessionException ) )
        {
            disconnect ();
        }
    }

    public synchronized void messageReceived ( final IoSession session, final Object message ) throws Exception
    {
        logger.debug ( "Message received: {}", message );
        checkSession ( session );

        if ( message instanceof DaveConnectionEstablishedMessage )
        {
            // we must we till we received this message ... now we can trigger
            // the job manager
            logger.info ( "DAVE Connection established " );
            this.jobManager.setSession ( this.session );
        }
        else if ( message instanceof DaveMessage )
        {
            this.jobManager.messageReceived ( (DaveMessage)message );
        }
        else if ( message instanceof DaveGenericMessage )
        {
            logger.info ( "Message received: {}", message );
        }
    }

    public synchronized void messageSent ( final IoSession session, final Object message ) throws Exception
    {
        logger.debug ( "Message sent: {}", message );
        checkSession ( session );
    }

    protected void checkSession ( final IoSession session )
    {
        if ( this.session != session && this.session != null )
        {
            logger.warn ( "Wrong session called: {} <-> {}", this.session, session );
            throw new WrongSessionException ();
        }
    }

    public synchronized void sessionClosed ( final IoSession session ) throws Exception
    {
        logger.warn ( "Connection lost: {}", session );
        if ( this.session != null )
        {
            checkSession ( session );
        }

        disconnected ();
    }

    public synchronized void sessionCreated ( final IoSession session ) throws Exception
    {
        logger.info ( "Session created: {}", session );

        logger.info ( "Setting reader timeout: {} / {}", this.readTimeout, session );
        session.getConfig ().setReaderIdleTime ( this.readTimeout / 1000 );
    }

    public synchronized void sessionIdle ( final IoSession session, final IdleStatus status ) throws Exception
    {
        logger.warn ( "Got idle: {} / {}", status, session );

        checkSession ( session );

        disconnect ();
    }

    public synchronized void sessionOpened ( final IoSession session ) throws Exception
    {
        logger.info ( "Session opened: {}", session );

        if ( this.session != null )
        {
            checkSession ( session );
        }

        setSession ( session );
    }

    public Executor getExecutor ()
    {
        return this.executor;
    }

    public String getId ()
    {
        return this.id;
    }

    public void writeBit ( final DaveRequestBlock block, final int index, final int subIndex, final boolean value )
    {
        logger.info ( "Bit write request - index: {}.{} -> {}", new Object[] { index, subIndex, value } );
        final DaveWriteRequest request = new DaveWriteRequest ();

        request.addRequest ( new DaveWriteRequest.BitRequest ( block.getRequest ().getArea (), block.getRequest ().getBlock (), (short) ( index * 8 + subIndex ), value ) );

        this.jobManager.addWriteRequest ( request );
    }

    public void writeWord ( final DaveRequestBlock block, final int index, final short value )
    {
        logger.info ( "Word write request - index: {} -> {}", new Object[] { index, value } );
        final DaveWriteRequest request = new DaveWriteRequest ();

        final IoBuffer data = IoBuffer.allocate ( 2 );
        data.putShort ( value );
        data.flip ();

        request.addRequest ( new DaveWriteRequest.ByteRequest ( block.getRequest ().getArea (), block.getRequest ().getBlock (), (short)index, data ) );

        this.jobManager.addWriteRequest ( request );
    }

    public void writeFloat ( final DaveRequestBlock block, final int index, final float value )
    {
        logger.info ( "Float write request - index: {} -> {}", new Object[] { index, value } );
        final DaveWriteRequest request = new DaveWriteRequest ();

        final IoBuffer data = IoBuffer.allocate ( 4 );
        data.putFloat ( value );
        data.flip ();

        request.addRequest ( new DaveWriteRequest.ByteRequest ( block.getRequest ().getArea (), block.getRequest ().getBlock (), (short)index, data ) );

        this.jobManager.addWriteRequest ( request );
    }

    public void addBlock ( final String name, final DaveRequestBlock deviceBlock )
    {
        this.jobManager.addBlock ( name, deviceBlock );
    }

    public void removeBlock ( final String block )
    {
        this.jobManager.removeBlock ( block );
    }

}
