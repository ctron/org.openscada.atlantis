/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.core.net;

import java.security.SecureRandom;

import javax.net.ssl.SSLContext;

import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.compression.CompressionFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.openscada.core.ConnectionInformation;
import org.openscada.net.mina.GMPPProtocolDecoder;
import org.openscada.net.mina.GMPPProtocolEncoder;

public class ConnectionHelper
{

    private static Logger logger = Logger.getLogger ( ConnectionHelper.class );

    /**
     * Setup the filter chain of a NET/GMPP connection
     * @param connectionInformation the connection information to use
     * @param filterChainBuilder the chain builder
     */
    public static void setupFilterChain ( final ConnectionInformation connectionInformation, final DefaultIoFilterChainBuilder filterChainBuilder )
    {
        // set up compression
        final String compress = connectionInformation.getProperties ().get ( "compress" );
        if ( compress != null )
        {
            filterChainBuilder.addLast ( "compress", new CompressionFilter () );
        }

        // set up ssl
        final String ssl = connectionInformation.getProperties ().get ( "ssl" );
        if ( ssl != null )
        {

            String sslProtocol = connectionInformation.getProperties ().get ( "sslProtocol" );
            if ( sslProtocol == null || sslProtocol.length () == 0 )
            {
                sslProtocol = "SSLv3";
            }

            final String sslRandom = connectionInformation.getProperties ().get ( "sslRandom" );

            SSLContext sslContext = null;
            try
            {
                sslContext = SSLContext.getInstance ( sslProtocol );

                SecureRandom random = null;
                if ( sslRandom != null && sslRandom.length () == 0 )
                {
                    random = SecureRandom.getInstance ( sslRandom );
                }

                sslContext.init ( null, null, random );
            }
            catch ( final Throwable e )
            {
                logger.warn ( "Failed to enable SSL" );
            }

            if ( sslContext != null )
            {
                filterChainBuilder.addLast ( "ssl", new SslFilter ( sslContext ) );
            }
        }

        // set up logging
        final String trace = connectionInformation.getProperties ().get ( "trace" );
        if ( trace != null )
        {
            filterChainBuilder.addLast ( "logging", new LoggingFilter () );
        }

        // add the main codec
        filterChainBuilder.addLast ( "codec", new ProtocolCodecFilter ( new GMPPProtocolEncoder (), new GMPPProtocolDecoder () ) );
    }
}
