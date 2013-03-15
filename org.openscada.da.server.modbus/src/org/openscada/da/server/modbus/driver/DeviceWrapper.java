/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.modbus.driver;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.core.Variant;
import org.openscada.core.server.OperationParameters;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;
import org.openscada.da.server.common.chain.item.ChainCreator;
import org.openscada.da.server.io.common.BaseDevice;
import org.openscada.da.server.io.common.BaseDeviceWrapper;
import org.openscada.da.server.modbus.Device;
import org.openscada.da.server.modbus.DeviceImpl;
import org.openscada.da.server.modbus.DeviceListener;
import org.openscada.da.server.modbus.ModbusDeviceType;
import org.openscada.da.server.modbus.ModbusRegisterType;
import org.openscada.da.server.modbus.Rs232Information;
import org.openscada.da.server.modbus.driver.SlaveDevice.Block;
import org.openscada.da.server.modbus.driver.SlaveDevice.ItemValue;
import org.openscada.da.server.modbus.driver.SlaveDevice.Tag;
import org.openscada.da.server.modbus.io.message.RequestWrapper;
import org.openscada.da.server.modbus.io.message.ResponseWrapper;
import org.openscada.da.server.modbus.io.message.request.AddressableRequestMessage;
import org.openscada.da.server.modbus.io.message.request.ReadCoilsRequest;
import org.openscada.da.server.modbus.io.message.request.ReadDiscreteInputsRequest;
import org.openscada.da.server.modbus.io.message.request.ReadHoldingRegistersRequest;
import org.openscada.da.server.modbus.io.message.request.ReadInputRegistersRequest;
import org.openscada.da.server.modbus.io.message.request.RequestMessage;
import org.openscada.da.server.modbus.io.message.request.WriteMultipleRegistersRequest;
import org.openscada.da.server.modbus.io.message.request.WriteSingleCoilRequest;
import org.openscada.da.server.modbus.io.message.response.ReadCoilsResponse;
import org.openscada.da.server.modbus.io.message.response.ReadDiscreteInputsResponse;
import org.openscada.da.server.modbus.io.message.response.ReadHoldingRegistersResponse;
import org.openscada.da.server.modbus.io.message.response.ReadInputRegistersResponse;
import org.openscada.utils.collection.MapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceWrapper extends BaseDeviceWrapper implements DeviceListener
{
    private static final Logger logger = LoggerFactory.getLogger ( DeviceWrapper.class );

    private static final long DATA_TIMEOUT = 30 * 1000;

    private static final long REQUEST_TIMEOUT = 1 * 1000;

    private final ConcurrentMap<Byte, SlaveDevice> slaves = new ConcurrentHashMap<Byte, SlaveDevice> ();

    private final ConcurrentMap<Byte, List<Block>> queryBlocks = new ConcurrentHashMap<Byte, List<Block>> ();

    private final ScheduledExecutorService scheduler;

    private final ModbusDeviceType deviceType;

    private final Rs232Information rs232Information;

    private final SocketAddress address;

    private final long interFrameDelay;

    private final long interCharacterTimeout;

    private Device device;

    private CommandState commandState = CommandState.IDLE;

    private final Queue<RequestWrapper> requestQueue = new LinkedList<RequestWrapper> ();

    private long priorityCounter = 0;

    // items
    private final DataItemInputChained commandStateItem;

    private final ConcurrentMap<Byte, Map<String, DataItemInputChained>> items = new ConcurrentHashMap<Byte, Map<String, DataItemInputChained>> ();

    public DeviceWrapper ( final Hive hive, final String deviceTag, final ScheduledExecutorService scheduler, final FolderCommon rootFolder, final InetSocketAddress address, final ModbusDeviceType deviceType, final Rs232Information rs232Information, final long interFrameDelay, final long interCharacterTimeout, final Map<Byte, SlaveDevice> slaves )
    {
        super ( hive, deviceTag, scheduler, rootFolder );
        this.slaves.putAll ( slaves );

        // setup fields
        this.scheduler = scheduler;
        this.deviceType = deviceType;
        this.rs232Information = rs232Information;
        this.address = address;
        this.interFrameDelay = interFrameDelay;
        this.interCharacterTimeout = interCharacterTimeout;

        // setup items
        this.commandStateItem = createInput ( "commandState" );
        initItems ();

        // TODO: move to start()
        initialize ();
        logger.trace ( "hive initialized" );
    }

    @Override
    protected BaseDevice createDevice ()
    {
        this.device = new DeviceImpl ( this.scheduler, this.address, this.deviceType, this.rs232Information, this.interFrameDelay, this.interCharacterTimeout, this.requestQueue, REQUEST_TIMEOUT );
        this.device.addDeviceListener ( this );
        return this.device;
    }

    @Override
    protected long getTimeoutValue ()
    {
        return DATA_TIMEOUT;
    }

    @Override
    protected void onTimeout ( final boolean timeout )
    {
        super.onTimeout ( timeout );
        if ( timeout )
        {
            setCommandState ( CommandState.IDLE );
        }
    }

    @Override
    protected void tick ()
    {
        logger.trace ( "tick ()" );
        super.tick ();
        // no requests on queue, so schedule the whole bunch
        if ( this.requestQueue.size () == 0 )
        {
            scheduleBlockRequests ();
        }
        if ( isConnected () && this.commandState == CommandState.IDLE )
        {
            setCommandState ( CommandState.REQUEST );
            this.device.sendMessages ( this.requestQueue );
        }
        if ( isTimeoutCondition () && this.commandState != CommandState.IDLE )
        {
            setCommandState ( CommandState.IDLE );
        }
        updateTimeouts ();
    }

    // /////////////////////////////////////////////////////////////////////
    // called by DeviceImpl
    // /////////////////////////////////////////////////////////////////////

    @Override
    public void messageQueueEmpty ()
    {
        logger.trace ( "messageQueueEmpty ()" );
        setCommandState ( CommandState.IDLE );
    }

    @Override
    public void onMessageReceived ( final ResponseWrapper responseWrapper )
    {
        logger.trace ( "onMessageReceived () {}", responseWrapper );
        tickLastInput ();
        final SlaveDevice slave = this.slaves.get ( responseWrapper.getUnitIdentifier () );
        if ( responseWrapper.getOriginalRequest () instanceof AddressableRequestMessage )
        {
            final int startAddress = ( (AddressableRequestMessage)responseWrapper.getOriginalRequest () ).getStartAddress ();
            final int quantity = ( (AddressableRequestMessage)responseWrapper.getOriginalRequest () ).getQuantity ();
            if ( responseWrapper.getMessage () instanceof ReadDiscreteInputsResponse )
            {
                final Set<ItemValue> results = slave.updateBitFields ( ModbusRegisterType.DiscreteInputs, startAddress, quantity, ( (ReadDiscreteInputsResponse)responseWrapper.getMessage () ).getBitset () );
                notifyItems ( responseWrapper.getUnitIdentifier (), results );
            }
            else if ( responseWrapper.getMessage () instanceof ReadCoilsResponse )
            {
                final Set<ItemValue> results = slave.updateBitFields ( ModbusRegisterType.Coils, startAddress, quantity, ( (ReadCoilsResponse)responseWrapper.getMessage () ).getBitset () );
                notifyItems ( responseWrapper.getUnitIdentifier (), results );
            }
            else if ( responseWrapper.getMessage () instanceof ReadInputRegistersResponse )
            {
                final Set<ItemValue> results = slave.updateRegisters ( ModbusRegisterType.InputRegisters, startAddress, quantity, ( (ReadInputRegistersResponse)responseWrapper.getMessage () ).getRegisters () );
                notifyItems ( responseWrapper.getUnitIdentifier (), results );
            }
            else if ( responseWrapper.getMessage () instanceof ReadHoldingRegistersResponse )
            {
                final Set<ItemValue> results = slave.updateRegisters ( ModbusRegisterType.HoldingRegisters, startAddress, quantity, ( (ReadHoldingRegistersResponse)responseWrapper.getMessage () ).getRegisters () );
                notifyItems ( responseWrapper.getUnitIdentifier (), results );
            }
        }
    }

    @Override
    public void onError ( final Throwable cause )
    {
        logger.warn ( "onError () {}", cause );
        setCommandState ( CommandState.IDLE );
    }

    @Override
    public void onMessageSent ( final RequestWrapper requestWrapper )
    {
        logger.trace ( "onMessageSent () {}", requestWrapper );
        setCommandState ( CommandState.RESPONSE );
    }

    // /////////////////////////////////////////////////////////////////////
    // private methods
    // /////////////////////////////////////////////////////////////////////

    /**
     * create all items
     */
    private void initItems ()
    {
        for ( final byte unitIdentifier : this.slaves.keySet () )
        {
            final SlaveDevice slave = this.slaves.get ( unitIdentifier );

            final FolderCommon folder = new FolderCommon ();
            this.baseFolder.add ( slave.getName (), folder, new MapBuilder<String, Variant> ().put ( "unitIdentifier", Variant.valueOf ( slave.getUnitIdentifier () ) ).getMap () );
            final Map<String, DataItemInputChained> slaveItems = new ConcurrentHashMap<String, DataItemInputChained> ();
            this.items.put ( slave.getUnitIdentifier (), slaveItems );

            for ( final Tag tag : slave.getTagsByRegistertType ( ModbusRegisterType.DiscreteInputs ) )
            {
                final DataItemInputChained item = createInput ( folder, slave, tag );
                slaveItems.put ( tag.getName (), item );
            }
            for ( final Tag tag : slave.getTagsByRegistertType ( ModbusRegisterType.Coils ) )
            {
                final WriteHandlerItem item = createInputOutput ( folder, slave, tag );
                item.setWriteHandler ( new WriteHandler () {
                    @Override
                    public void handleWrite ( final Variant value, final OperationParameters operationParameters ) throws Exception
                    {
                        DeviceWrapper.this.requestQueue.add ( new RequestWrapper ( unitIdentifier, new WriteSingleCoilRequest ( tag.getAddress (), slave.getCoilOffset (), value ) ) );
                        DeviceWrapper.this.requestQueue.add ( new RequestWrapper ( unitIdentifier, new ReadCoilsRequest ( tag.getAddress (), slave.getCoilOffset (), 1 ) ) );
                    }
                } );
                slaveItems.put ( tag.getName (), item );
            }
            for ( final Tag tag : slave.getTagsByRegistertType ( ModbusRegisterType.InputRegisters ) )
            {
                final DataItemInputChained item = createInput ( folder, slave, tag );
                slaveItems.put ( tag.getName (), item );
            }
            for ( final Tag tag : slave.getTagsByRegistertType ( ModbusRegisterType.HoldingRegisters ) )
            {
                final WriteHandlerItem item = createInputOutput ( folder, slave, tag );
                item.setWriteHandler ( new WriteHandler () {
                    @Override
                    public void handleWrite ( final Variant value, final OperationParameters operationParameters ) throws Exception
                    {
                        DeviceWrapper.this.requestQueue.add ( new RequestWrapper ( unitIdentifier, new WriteMultipleRegistersRequest ( tag.getAddress (), slave.getHoldingRegisterOffset (), value, tag.getType () ) ) );
                        DeviceWrapper.this.requestQueue.add ( new RequestWrapper ( unitIdentifier, new ReadHoldingRegistersRequest ( tag.getAddress (), slave.getHoldingRegisterOffset (), tag.getType ().getSize () ) ) );
                    }
                } );
                slaveItems.put ( tag.getName (), item );
            }

            // after all items have been created, they are split into blocks
            // which can be queried in one go
            this.queryBlocks.put ( unitIdentifier, slave.calculateBlocks ( slave.getDiscreteInputOffset (), slave.getCoilOffset (), slave.getInputRegisterOffset (), slave.getHoldingRegisterOffset () ) );
        }
    }

    /**
     * create all requests and put them on the request queue for late processing
     */
    private void scheduleBlockRequests ()
    {
        this.priorityCounter += 1;
        for ( final Byte unitIdentifier : this.queryBlocks.keySet () )
        {
            final List<Block> blocks = this.queryBlocks.get ( unitIdentifier );
            for ( final Block block : blocks )
            {
                logger.trace ( "schedule block {}", block );
                final RequestMessage request;
                switch ( block.getRegisterType () )
                {
                    case DiscreteInputs:
                        request = new ReadDiscreteInputsRequest ( block.getStartAddress (), block.getOffset (), block.getQuantity () );
                        break;
                    case Coils:
                        request = new ReadCoilsRequest ( block.getStartAddress (), block.getOffset (), block.getQuantity () );
                        break;
                    case InputRegisters:
                        request = new ReadInputRegistersRequest ( block.getStartAddress (), block.getOffset (), block.getQuantity () );
                        break;
                    case HoldingRegisters:
                        request = new ReadHoldingRegistersRequest ( block.getStartAddress (), block.getOffset (), block.getQuantity () );
                        break;
                    default:
                        throw new IllegalArgumentException ( "registerType unknown" );
                }
                if ( this.priorityCounter % block.getPriority () == 0 )
                {
                    this.requestQueue.add ( new RequestWrapper ( unitIdentifier, request ) );
                }
            }
        }
    }

    /**
     * @param unitIdentifier
     *            slaveId
     * @param results
     *            values which are to be sent to the client
     */
    private void notifyItems ( final byte unitIdentifier, final Set<ItemValue> results )
    {
        final Map<String, DataItemInputChained> slaveItems = this.items.get ( unitIdentifier );
        for ( final ItemValue itemValue : results )
        {
            slaveItems.get ( itemValue.getName () ).updateData ( itemValue.getValue (), new HashMap<String, Variant> (), AttributeMode.UPDATE );
        }
    }

    /**
     * set timeout for all items
     */
    private void updateTimeouts ()
    {
        for ( final Map<String, DataItemInputChained> slaveItems : this.items.values () )
        {
            for ( final DataItemInputChained item : slaveItems.values () )
            {
                final Map<String, Variant> attr = new HashMap<String, Variant> ();
                attr.put ( "timeout.error", Variant.valueOf ( isTimeoutCondition () ) );
                item.updateData ( null, attr, AttributeMode.UPDATE );
            }
        }
    }

    private void setCommandState ( final CommandState commandState )
    {
        this.commandState = commandState;
        this.commandStateItem.updateData ( Variant.valueOf ( commandState.name () ), null, null );
    }

    public String getItemId ( final SlaveDevice slaveDevice, final String item )
    {
        return this.deviceTag + "." + slaveDevice.getName () + "." + item;
    }

    /**
     * create a new input item
     * 
     * @param tag
     *            the item name
     * @return the input item
     */
    protected DataItemInputChained createInput ( final FolderCommon folder, final SlaveDevice slaveDevice, final Tag tag )
    {
        final DataItemInputChained item = new DataItemInputChained ( getItemId ( slaveDevice, tag.getName () ), getHive ().getOperationService () );
        getHive ().registerItem ( item );
        item.updateData ( Variant.NULL, new MapBuilder<String, Variant> ().put ( "address.dec", Variant.valueOf ( tag.getAddress () ) ).put ( "address.hex", Variant.valueOf ( String.format ( "%x", tag.getAddress () ) ) ).getMap (), AttributeMode.SET );
        ChainCreator.applyDefaultInputChain ( item, getHive () );
        folder.add ( tag.getName (), item, new MapBuilder<String, Variant> ().getMap () );
        return item;
    }

    /**
     * create a new input/output item
     * 
     * @param tag
     *            the item name
     * @return the input/output item
     */
    protected WriteHandlerItem createInputOutput ( final FolderCommon folder, final SlaveDevice slaveDevice, final Tag tag )
    {
        final WriteHandlerItem item = new WriteHandlerItem ( getItemId ( slaveDevice, tag.getName () ), null, getHive ().getOperationService () );
        getHive ().registerItem ( item );
        item.updateData ( Variant.NULL, new MapBuilder<String, Variant> ().put ( "address.dec", Variant.valueOf ( tag.getAddress () ) ).put ( "address.hex", Variant.valueOf ( String.format ( "%x", tag.getAddress () ) ) ).getMap (), AttributeMode.SET );
        ChainCreator.applyDefaultInputChain ( item, getHive () );
        folder.add ( tag.getName (), item, new MapBuilder<String, Variant> ().getMap () );
        return item;
    }
}
