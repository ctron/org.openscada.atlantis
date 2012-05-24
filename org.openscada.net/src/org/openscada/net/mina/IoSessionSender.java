/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.net.mina;

import org.apache.mina.core.session.IoSession;
import org.openscada.core.info.StatisticsImpl;
import org.openscada.net.base.data.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IoSessionSender implements MessageSender
{
    private static final long MAX_SEQUENCE = 0x7FFFFFFF;

    private static final long INIT_SEQUENCE = 1;

    private long sequence = INIT_SEQUENCE;

    private final IoSession session;

    public static Object STATS_QUEUED_BYTES = new Object ();

    private final StatisticsImpl statistics;

    private final static Logger logger = LoggerFactory.getLogger ( IoSessionSender.class );

    public IoSessionSender ( final IoSession session, final StatisticsImpl statistics )
    {
        this.session = session;
        this.statistics = statistics;
        statistics.setLabel ( STATS_QUEUED_BYTES, "Scheduled write bytes" );
    }

    @Override
    public synchronized boolean sendMessage ( final Message message, final PrepareSendHandler handler )
    {
        message.setSequence ( nextSequence () );

        // if we have a prepare send handler .. notify
        if ( handler != null )
        {
            handler.prepareSend ( message );
        }

        this.session.write ( message );

        logger.trace ( "Scheduled write bytes: {}", this.session.getScheduledWriteBytes () );
        this.statistics.setCurrentValue ( STATS_QUEUED_BYTES, this.session.getScheduledWriteBytes () );

        return true;
    }

    private long nextSequence ()
    {
        final long seq = this.sequence++;
        if ( this.sequence >= MAX_SEQUENCE )
        {
            this.sequence = INIT_SEQUENCE;
        }
        return seq;
    }

    @Override
    public void close ()
    {
        this.session.close ( true );
    }

    @Override
    public String toString ()
    {
        return this.session.toString ();
    }

}
