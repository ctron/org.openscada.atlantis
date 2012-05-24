/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.core.server.net;

import java.util.Collection;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.multiton.SingleSessionIoHandler;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.info.StatisticEntry;
import org.openscada.core.info.StatisticsImpl;
import org.openscada.net.base.PingService;
import org.openscada.net.base.data.Message;
import org.openscada.net.mina.IoSessionSender;
import org.openscada.net.mina.Messenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServerConnectionHandler implements SingleSessionIoHandler
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractServerConnectionHandler.class );

    private static final Object STATS_PINGS_SENT = new Object ();

    private static final Object STATS_SESSION_BYTES_READ = new Object ();

    private static final Object STATS_SESSION_BYTES_WRITTEN = new Object ();

    private static final int DEFAULT_TIMEOUT = 10000;

    protected IoSession ioSession;

    protected final Messenger messenger;

    protected final PingService pingService;

    protected final ConnectionInformation connectionInformation;

    private final StatisticsImpl statistics;

    private ManagedConnection mxBean;

    public AbstractServerConnectionHandler ( final IoSession ioSession, final ConnectionInformation connectionInformation )
    {
        super ();
        this.ioSession = ioSession;
        this.connectionInformation = connectionInformation;

        this.statistics = new StatisticsImpl ();

        this.messenger = new Messenger ( getMessageTimeout (), this.statistics );

        this.pingService = new PingService ( this.messenger );
        this.pingService.start ();

        this.ioSession.getConfig ().setReaderIdleTime ( getPingPeriod () / 1000 );

        this.messenger.connected ( new IoSessionSender ( this.ioSession, this.statistics ) );

        this.mxBean = ManagedConnection.register ( new ManagedConnection () {
            @Override
            protected Collection<StatisticEntry> getEntries ()
            {
                return AbstractServerConnectionHandler.this.statistics.getEntries ();
            }

            @Override
            public void close ()
            {
                AbstractServerConnectionHandler.this.ioSession.close ( false );
            }
        }, ioSession.getRemoteAddress () );

        this.statistics.setLabel ( STATS_PINGS_SENT, "Pings sent" );
        this.statistics.setLabel ( STATS_SESSION_BYTES_READ, "Bytes read in session" );
        this.statistics.setLabel ( STATS_SESSION_BYTES_WRITTEN, "Bytes written in session" );
    }

    @Override
    public void exceptionCaught ( final Throwable cause ) throws Exception
    {
        logger.warn ( "Something failed", cause );
    }

    @Override
    public void messageReceived ( final Object message ) throws Exception
    {
        if ( message instanceof Message )
        {
            this.statistics.setCurrentValue ( STATS_SESSION_BYTES_READ, this.ioSession.getReadBytes () );
            this.messenger.messageReceived ( (Message)message );
        }
    }

    @Override
    public void messageSent ( final Object message ) throws Exception
    {
        this.statistics.setCurrentValue ( STATS_SESSION_BYTES_WRITTEN, this.ioSession.getWrittenBytes () );
        this.statistics.setCurrentValue ( IoSessionSender.STATS_QUEUED_BYTES, this.ioSession.getScheduledWriteBytes () );
    }

    @Override
    public void sessionClosed () throws Exception
    {
        cleanUp ();
    }

    protected void cleanUp ()
    {
        if ( this.mxBean != null )
        {
            this.mxBean.dispose ();
            this.mxBean = null;
        }

        if ( this.ioSession != null )
        {
            this.messenger.disconnected ();
            this.ioSession.close ( true );
            this.ioSession = null;
        }
    }

    @Override
    public void sessionCreated () throws Exception
    {
    }

    @Override
    public void sessionIdle ( final IdleStatus status ) throws Exception
    {
        this.pingService.sendPing ();
        this.statistics.changeCurrentValue ( STATS_PINGS_SENT, 1 );
    }

    @Override
    public void sessionOpened () throws Exception
    {
    }

    public int getPingPeriod ()
    {
        return getIntProperty ( "pingPeriod", getIntProperty ( "timeout", DEFAULT_TIMEOUT ) / getIntProperty ( "pingFrequency", 3 ) );
    }

    public int getMessageTimeout ()
    {
        return getIntProperty ( "messageTimeout", getIntProperty ( "timeout", DEFAULT_TIMEOUT ) );
    }

    protected int getIntProperty ( final String propertyName, final int defaultValue )
    {
        try
        {
            final String timeout = this.connectionInformation.getProperties ().get ( propertyName );
            final int i = Integer.parseInt ( timeout );
            if ( i <= 0 )
            {
                return defaultValue;
            }
            return i;
        }
        catch ( final Throwable e )
        {
            return defaultValue;
        }
    }
}