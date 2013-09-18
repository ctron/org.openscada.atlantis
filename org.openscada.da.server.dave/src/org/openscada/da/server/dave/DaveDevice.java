/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 IBH SYSTEMS GmbH (http://ibh-systems.com)
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.eclipse.scada.core.Variant;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.io.AbstractConnectionDevice;
import org.openscada.da.server.common.memory.AbstractRequestBlock;
import org.openscada.protocols.dave.DaveConnectionEstablishedMessage;
import org.openscada.protocols.dave.DaveFilter;
import org.openscada.protocols.dave.DaveGenericMessage;
import org.openscada.protocols.dave.DaveMessage;
import org.openscada.protocols.dave.DaveWriteRequest;
import org.openscada.protocols.iso8073.COTPFilter;
import org.openscada.protocols.tkpt.TPKTFilter;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaveDevice extends AbstractConnectionDevice
{

    private final static Logger logger = LoggerFactory.getLogger ( DaveDevice.class );

    private Integer rack;

    private Byte slot;

    private final DataItemInputChained configItem;

    private final DaveBlockConfigurator configurator;

    private final DaveJobManager jobManager;

    private int readTimeout;

    public DaveDevice ( final BundleContext context, final String id, final Map<String, String> properties ) throws Exception
    {
        super ( context, id, "DaveDevice", "dave" );

        this.jobManager = new DaveJobManager ( this );

        final Map<String, Variant> props = new HashMap<String, Variant> ();

        this.configItem = this.itemFactory.createInput ( "config", props );

        /*
         * FIXME: this call is needed since the block configurator might immediately
         * trigger changes which then call to an unconfigured DaveDevice. This should
         * be handled in a start() call instead.
         */

        configure ( properties );

        this.configurator = new DaveBlockConfigurator ( this, this.context );
    }

    @Override
    public String getItemId ( final String localId )
    {
        return super.getItemId ( localId );
    }

    @Override
    protected void performDispose ()
    {
        this.configurator.dispose ();

        this.jobManager.dispose ();

        super.performDispose ();
    }

    @Override
    protected void configure ( final Map<String, String> properties ) throws Exception
    {
        logger.info ( "Applying configuration: {}", properties );

        this.rack = Integer.valueOf ( properties.get ( "rack" ) );
        this.slot = Byte.valueOf ( properties.get ( "slot" ) );
        this.readTimeout = getTimeout ( properties, "readTimeout", 5000/*ms*/);

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "host", Variant.valueOf ( properties.get ( "host" ) ) );
        attributes.put ( "port", Variant.valueOf ( properties.get ( "port" ) ) );
        attributes.put ( "rack", Variant.valueOf ( this.rack ) );
        attributes.put ( "slot", Variant.valueOf ( this.slot ) );
        attributes.put ( "name", Variant.valueOf ( properties.get ( "name" ) ) );
        this.configItem.updateData ( Variant.TRUE, attributes, AttributeMode.SET );

        super.configure ( properties );
    }

    @Override
    protected void configureConnector ( final NioSocketConnector connector )
    {
        connector.getFilterChain ().addLast ( "tpkt", new TPKTFilter ( 3 ) );
        connector.getFilterChain ().addLast ( "cotp", new COTPFilter ( this.rack, this.slot ) );
        connector.getFilterChain ().addLast ( "dave", new DaveFilter () );
    }

    @Override
    public ScheduledExecutorService getExecutor ()
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

        this.jobManager.addWriteRequest ( request, 0 );
    }

    public void writeData ( final DaveRequestBlock block, final int index, final byte[] data )
    {
        final DaveWriteRequest request = new DaveWriteRequest ();
        request.addRequest ( new DaveWriteRequest.ByteRequest ( block.getRequest ().getArea (), block.getRequest ().getBlock (), (short)index, data ) );
        this.jobManager.addWriteRequest ( request, 0 );
    }

    public void addBlock ( final String name, final AbstractRequestBlock deviceBlock )
    {
        this.jobManager.addBlock ( name, deviceBlock );
    }

    public void removeBlock ( final String block )
    {
        this.jobManager.removeBlock ( block );
    }

    @Override
    protected synchronized void handleSessionCreated ( final IoSession session ) throws Exception
    {
        super.handleSessionCreated ( session );

        logger.info ( "Setting reader timeout: {} / {}", this.readTimeout, session );
        session.getConfig ().setReaderIdleTime ( this.readTimeout / 1000 );
    }

    @Override
    protected void handleSessionDisconnected ()
    {
        this.jobManager.setSession ( null );
    }

    @Override
    protected synchronized void handleMessageReceived ( final IoSession session, final Object message ) throws Exception
    {
        super.handleMessageReceived ( session, message );

        if ( message instanceof DaveConnectionEstablishedMessage )
        {
            // we must we till we received this message ... now we can trigger
            // the job manager
            logger.info ( "DAVE Connection established" );
            this.jobManager.setSession ( session );
        }
        else if ( message instanceof DaveMessage )
        {
            this.jobManager.messageReceived ( message );
        }
        else if ( message instanceof DaveGenericMessage )
        {
            logger.info ( "Message received: {}", message );
        }
    }

}
