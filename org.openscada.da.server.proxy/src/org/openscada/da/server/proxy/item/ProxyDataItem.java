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

package org.openscada.da.server.proxy.item;

import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.NoConnectionException;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.NotifyFuture;

/**
 * @author Juergen Rose &lt;juergen.rose@th4-systems.com&gt;
 *
 */
public class ProxyDataItem extends DataItemInputOutputChained
{
    private final ProxyValueHolder proxyValueHolder;

    private final ProxyWriteHandlerImpl writeHandler;

    /**
     * @param id
     * @param proxyValueHolder
     * @param executor the executor to use for write calls
     */
    public ProxyDataItem ( final String id, final ProxyValueHolder proxyValueHolder, final ProxyWriteHandlerImpl writeHandler, final Executor executor )
    {
        super ( new DataItemInformationBase ( id, EnumSet.allOf ( IODirection.class ) ), executor );
        this.proxyValueHolder = proxyValueHolder;
        this.proxyValueHolder.setListener ( new ItemUpdateListener () {
            @Override
            public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
            {
                ProxyDataItem.this.updateData ( value, attributes, cache ? AttributeMode.SET : AttributeMode.UPDATE );
            }

            @Override
            public void notifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
            {
                // TODO: (jr2) is there something which is to be done?
            }
        } );

        this.writeHandler = writeHandler;
    }

    /**
     * @return object which holds the actual data
     */
    public ProxyValueHolder getProxyValueHolder ()
    {
        return this.proxyValueHolder;
    }

    public ProxyWriteHandlerImpl getWriteHandler ()
    {
        return this.writeHandler;
    }

    @Override
    public NotifyFuture<WriteAttributeResults> startSetAttributes ( final Map<String, Variant> attributes, final OperationParameters operationParameters )
    {
        final FutureTask<WriteAttributeResults> task = new FutureTask<WriteAttributeResults> ( new Callable<WriteAttributeResults> () {

            @Override
            public WriteAttributeResults call () throws Exception
            {
                return processSetAttributes ( attributes, operationParameters );
            }
        } );
        this.executor.execute ( task );
        return task;
    }

    @Override
    protected WriteAttributeResults processSetAttributes ( final Map<String, Variant> attributes, final OperationParameters operationParameters )
    {
        final WriteAttributeResults writeAttributeResults = super.processSetAttributes ( attributes, operationParameters );
        // all attributes which could be successfully processed by chain must be ignored
        for ( final Entry<String, WriteAttributeResult> entry : writeAttributeResults.entrySet () )
        {
            if ( entry.getValue ().isSuccess () )
            {
                attributes.remove ( entry.getKey () );
            }
        }
        this.writeHandler.writeAttributes ( getInformation ().getName (), attributes, writeAttributeResults, operationParameters );
        return writeAttributeResults;
    }

    /**
     * @param attributes
     */
    public void setTemplateAttributes ( final Map<String, Variant> attributes, final OperationParameters operationParameters )
    {
        super.processSetAttributes ( attributes, operationParameters );
    }

    @Override
    protected NotifyFuture<WriteResult> startWriteCalculatedValue ( final Variant value, final OperationParameters operationParameters )
    {
        final FutureTask<WriteResult> task = new FutureTask<WriteResult> ( new Callable<WriteResult> () {

            @Override
            public WriteResult call () throws Exception
            {
                processWriteCalculatedValue ( value, operationParameters );
                return new WriteResult ();
            }
        } );
        this.executor.execute ( task );
        return task;
    }

    protected void processWriteCalculatedValue ( final Variant value, final OperationParameters operationParameters ) throws NotConvertableException, InvalidOperationException
    {
        try
        {
            this.writeHandler.write ( getInformation ().getName (), value, operationParameters );
        }
        catch ( final NoConnectionException e )
        {
            throw new InvalidOperationException ();
        }
        catch ( final OperationException e )
        {
            throw new InvalidOperationException ();
        }
    }
}
