/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.core.server.ngp;

import java.util.Collection;
import java.util.Map;

import javax.net.ssl.SSLSession;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslFilter;
import org.openscada.core.info.StatisticEntry;
import org.openscada.core.info.StatisticsImpl;
import org.openscada.core.server.common.stats.ManagedConnection;
import org.openscada.protocol.ngp.common.StatisticsFilter;
import org.openscada.protocol.ngp.common.mc.MessageChannelFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServerConnection
{

    private final static Logger logger = LoggerFactory.getLogger ( ServerConnection.class );

    private static final Object STATS_MESSAGES_SENT = new Object ();

    private static final Object STATS_MESSAGES_RECEIVED = new Object ();

    private final IoSession session;

    protected final StatisticsImpl statistics;

    private ManagedConnection mxBean;

    private final Object writeLock = new Object ();

    public ServerConnection ( final IoSession session )
    {
        logger.info ( "Creating new server connection: {}", session );

        this.statistics = new StatisticsImpl ();

        this.session = session;

        this.mxBean = ManagedConnection.register ( new ManagedConnection () {
            @Override
            protected Collection<StatisticEntry> getEntries ()
            {
                return ServerConnection.this.statistics.getEntries ();
            }

            @Override
            public void close ()
            {
                ServerConnection.this.session.close ( false );
            }

            @Override
            public Map<String, String> getTransportProperties ()
            {
                final MessageChannelFilter mcf = (MessageChannelFilter)session.getFilterChain ().get ( MessageChannelFilter.class );
                if ( mcf != null )
                {
                    return mcf.getAcceptedProperties ();
                }
                else
                {
                    return null;
                }
            }
        }, session.getRemoteAddress (), "org.openscada.core.server.ngp" );

        this.statistics.setLabel ( STATS_MESSAGES_SENT, "Messages sent" );
        this.statistics.setLabel ( STATS_MESSAGES_RECEIVED, "Messages received" );

        session.setAttribute ( StatisticsFilter.STATS_KEY, this.statistics );
    }

    public void dispose ()
    {
        logger.info ( "Disposing server connection: {}", this.session );

        if ( this.mxBean != null )
        {
            this.mxBean.dispose ();
            this.mxBean = null;
        }

        requestClose ( true );
    }

    protected void sendMessage ( final Object message )
    {
        logger.trace ( "Sending message: {}", message );

        this.statistics.changeCurrentValue ( STATS_MESSAGES_SENT, 1 );

        synchronized ( this.writeLock )
        {
            // only one thread may write at a time, otherwise MINA's filters may get corrupted
            // also see https://issues.apache.org/jira/browse/DIRMINA-653
            this.session.write ( message );
        }
    }

    public void requestClose ( final boolean immediately )
    {
        this.session.close ( immediately );
    }

    public abstract void messageReceived ( Object message ) throws Exception;

    public void handleMessageReceived ( final Object message ) throws Exception
    {
        logger.trace ( "Message received: {}", message );

        this.statistics.changeCurrentValue ( STATS_MESSAGES_RECEIVED, 1 );
        messageReceived ( message );
    }

    public SSLSession getSslSession ()
    {
        final IoSession session = this.session;
        if ( session == null )
        {
            return null;
        }
        final Object sslSession = session.getAttribute ( SslFilter.SSL_SESSION );

        if ( sslSession instanceof SSLSession )
        {
            return (SSLSession)sslSession;
        }
        else
        {
            return null;
        }
    }

}
