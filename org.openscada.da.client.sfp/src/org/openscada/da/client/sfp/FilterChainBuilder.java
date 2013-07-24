/*
 * This file is part of the openSCADA project
 * 
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

package org.openscada.da.client.sfp;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.openscada.protocol.common.IoLoggerFilterChainBuilder;
import org.openscada.protocol.common.StatisticsFilter;
import org.openscada.protocol.sfp.ProtocolDecoderImpl;
import org.openscada.protocol.sfp.ProtocolEncoderImpl;

public class FilterChainBuilder implements IoLoggerFilterChainBuilder
{
    private String loggerName;

    @Override
    public void setLoggerName ( final String loggerName )
    {
        this.loggerName = loggerName;
    }

    public String getLoggerName ()
    {
        return this.loggerName;
    }

    @Override
    public void buildFilterChain ( final IoFilterChain chain )
    {
        if ( this.loggerName != null && Boolean.getBoolean ( "org.openscada.protocol.sfp.common.logger.raw" ) )
        {
            chain.addFirst ( "logger.raw", new LoggingFilter ( this.loggerName ) );
        }

        if ( !Boolean.getBoolean ( "org.openscada.protocol.sfp.common.disableStats" ) )
        {
            chain.addFirst ( StatisticsFilter.DEFAULT_NAME, new StatisticsFilter () );
        }

        if ( this.loggerName != null && Boolean.getBoolean ( "org.openscada.protocol.sfp.common.logger" ) )
        {
            chain.addFirst ( "logger", new LoggingFilter ( this.loggerName ) );
        }

        chain.addLast ( "closeidle", new IoFilterAdapter () {
            @Override
            public void sessionIdle ( final NextFilter nextFilter, final IoSession session, final IdleStatus status ) throws Exception
            {
                session.close ( true );
            }
        } );
        chain.addLast ( "codec", new ProtocolCodecFilter ( new ProtocolEncoderImpl (), new ProtocolDecoderImpl () ) );
    }
}
