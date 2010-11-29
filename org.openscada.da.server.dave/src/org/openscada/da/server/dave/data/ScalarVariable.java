/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.dave.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.mina.core.buffer.IoBuffer;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.item.LevelAlarmChainItem;
import org.openscada.da.server.common.chain.item.ManualErrorOverrideChainItem;
import org.openscada.da.server.common.chain.item.ManualOverrideChainItem;
import org.openscada.da.server.common.chain.item.NegateInputItem;
import org.openscada.da.server.common.chain.item.ScaleInputItem;
import org.openscada.da.server.common.chain.item.SumAlarmChainItem;
import org.openscada.da.server.common.chain.item.SumErrorChainItem;
import org.openscada.da.server.common.chain.item.SumPatternAttributesChainItem;
import org.openscada.da.server.dave.DaveDevice;
import org.openscada.da.server.dave.DaveRequestBlock;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public abstract class ScalarVariable implements Variable
{
    private final String name;

    protected final int index;

    private final Executor executor;

    protected DaveDataitem item;

    private ServiceRegistration handle;

    private final Attribute[] attributes;

    protected DaveDevice device;

    private int offset;

    protected DaveRequestBlock block;

    private final ObjectPoolImpl itemPool;

    public ScalarVariable ( final String name, final int index, final Executor executor, final ObjectPoolImpl itemPool, final Attribute... attributes )
    {
        this.name = name;
        this.index = index;
        this.executor = executor;
        this.attributes = attributes;
        this.itemPool = itemPool;
    }

    public void handleError ( final int errorCode )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        for ( final Attribute attr : this.attributes )
        {
            attr.handleError ( attributes );
        }

        attributes.put ( "device.error", Variant.TRUE );
        attributes.put ( "device.error.code", new Variant ( errorCode ) );

        this.item.updateData ( new Variant (), attributes, AttributeMode.SET );
    }

    public void handleDisconnect ()
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        for ( final Attribute attr : this.attributes )
        {
            attr.handleError ( attributes );
        }

        attributes.put ( "communcation.error", Variant.TRUE );

        this.item.updateData ( new Variant (), attributes, AttributeMode.SET );
    }

    public void handleFailure ( final Throwable e )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        for ( final Attribute attr : this.attributes )
        {
            attr.handleError ( attributes );
        }

        attributes.put ( "generic.error", Variant.TRUE );
        attributes.put ( "generic.error.message", new Variant ( e.getMessage () ) );

        this.item.updateData ( new Variant (), attributes, AttributeMode.SET );
    }

    public void start ( final String parentName, final BundleContext context, final DaveDevice device, final DaveRequestBlock block, final int offset )
    {
        this.device = device;
        this.offset = offset;
        this.block = block;

        for ( final Attribute attr : this.attributes )
        {
            attr.start ( device, block, offset );
        }

        String itemId;
        if ( parentName != null )
        {
            itemId = parentName + "." + this.name;
        }
        else
        {
            itemId = this.name;
        }
        this.item = new DaveDataitem ( itemId, this.executor, this );

        this.item.addChainElement ( IODirection.INPUT, new NegateInputItem ( null ) );
        this.item.addChainElement ( IODirection.INPUT, new ScaleInputItem ( null ) );
        this.item.addChainElement ( IODirection.INPUT, new ManualOverrideChainItem ( null ) );
        this.item.addChainElement ( IODirection.INPUT, new LevelAlarmChainItem ( null ) );
        this.item.addChainElement ( IODirection.INPUT, new SumAlarmChainItem ( null ) );
        this.item.addChainElement ( IODirection.INPUT, new SumErrorChainItem ( null ) );
        this.item.addChainElement ( IODirection.INPUT, new SumPatternAttributesChainItem ( null, "manual", ".*\\.manual\\.active$" ) );
        this.item.addChainElement ( IODirection.INPUT, new ManualErrorOverrideChainItem () );

        this.itemPool.addService ( itemId, this.item, null );
        // this.handle = context.registerService ( DataItem.class.getName (), this.item, null );

    }

    protected NotifyFuture<WriteResult> handleWrite ( final Variant value )
    {
        return new InstantErrorFuture<WriteResult> ( new IllegalStateException ( "Operation not implemented" ) );
    }

    public void stop ( final BundleContext context )
    {
        for ( final Attribute attr : this.attributes )
        {
            attr.stop ();
        }

        this.itemPool.removeService ( this.item.getInformation ().getName (), this.item );

        if ( this.handle != null )
        {
            this.handle.unregister ();
            this.handle = null;
        }
    }

    protected abstract Variant extractValue ( IoBuffer data, Map<String, Variant> attributes );

    public void handleData ( final IoBuffer data, final Variant timestamp )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        final Variant value = extractValue ( data, attributes );

        for ( final Attribute attr : this.attributes )
        {
            attr.handleData ( data, attributes, timestamp );
        }

        this.item.updateData ( value, attributes, AttributeMode.SET );
    }

    public int toAddress ( final int localAddress )
    {
        return this.offset + localAddress - this.block.getRequest ().getStart ();
    }

    public int toGlobalAddress ( final int localAddress )
    {
        return this.offset + localAddress;
    }

    public Map<String, WriteAttributeResult> handleAttributes ( final Map<String, Variant> requests )
    {
        final Map<String, WriteAttributeResult> result = new HashMap<String, WriteAttributeResult> ();

        for ( final Map.Entry<String, Variant> entry : requests.entrySet () )
        {
            for ( final Attribute attr : this.attributes )
            {
                if ( attr.getName ().equals ( entry.getKey () ) )
                {
                    try
                    {
                        attr.handleWrite ( entry.getValue () );
                        result.put ( entry.getKey (), WriteAttributeResult.OK );
                    }
                    catch ( final Throwable e )
                    {
                        result.put ( entry.getKey (), new WriteAttributeResult ( e ) );
                    }
                }
            }
        }

        return result;
    }
}
