/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.modbus;

import java.net.SocketAddress;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.openscada.da.server.io.common.StreamBaseDevice;
import org.openscada.da.server.modbus.io.ModbusRtuCodecFactory;
import org.openscada.da.server.modbus.io.ModbusTcpCodecFactory;
import org.openscada.da.server.modbus.io.ResetableCodecFactory;
import org.openscada.da.server.modbus.io.message.RequestWrapper;
import org.openscada.da.server.modbus.io.message.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceImpl extends StreamBaseDevice implements Device
{
    public enum DeviceState
    {
        IDLE,
        WAIT_FOR_REPLY;
    }

    private static final Logger logger = LoggerFactory.getLogger ( DeviceImpl.class );

    private final ScheduledExecutorService scheduler;

    private final ModbusDeviceType deviceType;

    @SuppressWarnings ( "unused" )
    private final Rs232Information rs232Information;

    @SuppressWarnings ( "unused" )
    private final long interCharacterTimeout; // in nanoseconds!

    private final long interFrameDelay; // in nanoseconds!

    private final Set<DeviceListener> listeners = new CopyOnWriteArraySet<DeviceListener> ();

    private final AtomicReference<DeviceState> deviceState = new AtomicReference<DeviceState> ( DeviceState.IDLE );

    final AtomicReference<ScheduledFuture<?>> submitterFuture = new AtomicReference<ScheduledFuture<?>> ( null );

    private ResetableCodecFactory codecFactory = null;

    private final Queue<RequestWrapper> requestQueue;

    final AtomicReference<ScheduledFuture<?>> cancelFuture = new AtomicReference<ScheduledFuture<?>> ( null );

    private long requestTimeout = 1000 * 1000 * 1000; // 1s timeout in ns

    public DeviceImpl ( final ScheduledExecutorService scheduler, final SocketAddress address, final ModbusDeviceType deviceType, final Rs232Information rs232Information, final long interFrameDelay, final long interCharacterTimeout, final Queue<RequestWrapper> requestQueue, final long timeout )
    {
        super ( address );
        this.scheduler = scheduler;
        this.deviceType = deviceType;
        this.rs232Information = rs232Information;
        this.interFrameDelay = interFrameDelay;
        this.interCharacterTimeout = interCharacterTimeout;
        this.requestQueue = requestQueue;
        if ( timeout > 0 )
        {
            this.requestTimeout = timeout * 1000 * 1000;
        }
    }

    @Override
    protected void setupConnector ( final SocketConnector connector )
    {
        logger.debug ( "setupConnector as " + this.deviceType + " (timeout=" + NumberFormat.getInstance ( Locale.US ).format ( this.interFrameDelay ) + "ns)" );
        connector.setConnectTimeoutMillis ( CONNECT_TIMEOUT );
        if ( this.deviceType == ModbusDeviceType.TCP )
        {
            this.codecFactory = new ModbusTcpCodecFactory ();
            connector.getFilterChain ().addLast ( "modbusFilter", new ProtocolCodecFilter ( this.codecFactory ) );
        }
        else if ( this.deviceType == ModbusDeviceType.RTU )
        {
            this.codecFactory = new ModbusRtuCodecFactory ( this.scheduler, this.interFrameDelay, TimeUnit.NANOSECONDS );
            connector.getFilterChain ().addLast ( "modbusFilter", new ProtocolCodecFilter ( this.codecFactory ) );
        }
        else
        {
            throw new IllegalArgumentException ( "ModbusDeviceType.ASCII is not implemented" );
        }
    }

    @Override
    public void messageReceived ( final IoSession session, final Object message ) throws Exception
    {
        logger.trace ( "messageReceived {}", message );
        this.deviceState.set ( DeviceState.IDLE );
        super.messageReceived ( session, message );
        if ( message instanceof ResponseWrapper )
        {
            for ( final DeviceListener listener : this.listeners )
            {
                listener.onMessageReceived ( (ResponseWrapper)message );
            }
        }
        final ScheduledFuture<?> cf = this.cancelFuture.get ();
        if ( cf != null )
        {
            cf.cancel ( false );
            this.cancelFuture.set ( null );
        }
    }

    @Override
    public void messageSent ( final IoSession session, final Object message ) throws Exception
    {
        logger.trace ( "messageSent {}", message );
        super.messageSent ( session, message );
        if ( message instanceof RequestWrapper )
        {
            for ( final DeviceListener listener : this.listeners )
            {
                listener.onMessageSent ( (RequestWrapper)message );
            }
        }
    }

    @Override
    public void exceptionCaught ( final IoSession session, final Throwable cause ) throws Exception
    {
        logger.warn ( "exceptionCaught ()", cause );
        cleanUp ( session );
        for ( final DeviceListener listener : this.listeners )
        {
            listener.onError ( cause );
        }
    }

    @Override
    public void sessionClosed ( final IoSession session ) throws Exception
    {
        logger.warn ( "sessionClosed ()" );
        super.sessionClosed ( session );
        cleanUp ( session );
    }

    ///////////////////////////////////////////////////////////////////////
    // called by DeviceWrapper
    ///////////////////////////////////////////////////////////////////////

    private void cleanUp ( final IoSession session )
    {
        final ScheduledFuture<?> sf = this.submitterFuture.get ();
        if ( sf != null )
        {
            sf.cancel ( true );
            this.submitterFuture.set ( null );
        }
        if ( session != null )
        {
            session.removeAttribute ( ModbusTcpCodecFactory.SESSION_KEY_CURRENT_REQUESTS );
        }
        DeviceImpl.this.codecFactory.reset ( session );
        this.requestQueue.clear ();
        this.deviceState.set ( DeviceState.IDLE );
    }

    @Override
    public void sendMessages ( final Queue<RequestWrapper> requestQueue )
    {
        logger.debug ( "sendMessages () with {} elements", requestQueue.size () );
        // timeout of deviceWrapper is always greater than this one, so it is safe to set device state to idle
        this.deviceState.set ( DeviceState.IDLE );
        final Runnable submitter = new Runnable () {
            @Override
            public void run ()
            {
                logger.trace ( "sendMessages () :: run () with deviceState = {}", DeviceImpl.this.deviceState.get () );
                if ( DeviceImpl.this.deviceState.get () == DeviceState.IDLE )
                {
                    final RequestWrapper requestWrapper = requestQueue.poll ();
                    if ( requestWrapper == null )
                    {
                        logger.trace ( "sendMessages () :: run () message queue empty " );
                        DeviceImpl.this.deviceState.set ( DeviceState.IDLE );
                        for ( final DeviceListener listener : DeviceImpl.this.listeners )
                        {
                            listener.messageQueueEmpty ();
                        }
                        if ( DeviceImpl.this.submitterFuture.get () == null )
                        {
                            DeviceImpl.this.submitterFuture.get ().cancel ( false );
                        }
                    }
                    else
                    {
                        sendMessage ( requestWrapper );
                    }
                }
            }
        };
        this.submitterFuture.set ( this.scheduler.scheduleAtFixedRate ( submitter, this.interFrameDelay, this.interFrameDelay, TimeUnit.NANOSECONDS ) );
    }

    private void sendMessage ( final RequestWrapper requestWrapper )
    {
        logger.trace ( "sendMessage () = {}", requestWrapper );
        this.deviceState.set ( DeviceState.WAIT_FOR_REPLY );
        this.session.write ( requestWrapper );
        this.cancelFuture.set ( this.scheduler.schedule ( new Runnable () {
            @Override
            public void run ()
            {
                logger.warn ( "request timed out = {}, reset request queue", requestWrapper );
                if ( Boolean.getBoolean ( "org.openscada.da.server.modbus.clearaftertimeout" ) )
                {
                    DeviceImpl.this.requestQueue.clear ();
                    DeviceImpl.this.deviceState.set ( DeviceState.IDLE );
                }
            }
        }, this.requestTimeout, TimeUnit.NANOSECONDS ) );
    }

    @Override
    public void addDeviceListener ( final DeviceListener deviceListener )
    {
        this.listeners.add ( deviceListener );
    }

    @Override
    public void removeDeviceListener ( final DeviceListener deviceListener )
    {
        this.listeners.remove ( deviceListener );
    }
}
