/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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
import org.openscada.core.server.common.session.UserSession;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.NotifyFuture;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class ProxyDataItem extends DataItemInputOutputChained
{
    private final ProxyValueHolder proxyValueHolder;

    private final ProxyWriteHandler writeHandler;

    /**
     * @param id
     * @param proxyValueHolder
     * @param executor the executor to use for write calls
     */
    public ProxyDataItem ( final String id, final ProxyValueHolder proxyValueHolder, final ProxyWriteHandler writeHandler, final Executor executor )
    {
        super ( new DataItemInformationBase ( id, EnumSet.allOf ( IODirection.class ) ), executor );
        this.proxyValueHolder = proxyValueHolder;
        this.proxyValueHolder.setListener ( new ItemUpdateListener () {
            public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
            {
                ProxyDataItem.this.updateData ( value, attributes, cache ? AttributeMode.SET : AttributeMode.UPDATE );
            }

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

    @Override
    public NotifyFuture<WriteAttributeResults> startSetAttributes ( final UserSession session, final Map<String, Variant> attributes )
    {
        final FutureTask<WriteAttributeResults> task = new FutureTask<WriteAttributeResults> ( new Callable<WriteAttributeResults> () {

            public WriteAttributeResults call () throws Exception
            {
                return processSetAttributes ( attributes );
            }
        } );
        this.executor.execute ( task );
        return task;
    }

    protected WriteAttributeResults processSetAttributes ( final Map<String, Variant> attributes )
    {
        final WriteAttributeResults writeAttributeResults = super.processSetAttributes ( attributes );
        // all attributes which could be successfully processed by chain must be ignored
        for ( final Entry<String, WriteAttributeResult> entry : writeAttributeResults.entrySet () )
        {
            if ( entry.getValue ().isSuccess () )
            {
                attributes.remove ( entry.getKey () );
            }
        }
        this.writeHandler.writeAttributes ( this.getInformation ().getName (), attributes, writeAttributeResults );
        return writeAttributeResults;
    }

    /**
     * @param attributes
     */
    public void setTemplateAttributes ( final Map<String, Variant> attributes )
    {
        super.processSetAttributes ( attributes );
    }

    @Override
    protected NotifyFuture<WriteResult> startWriteCalculatedValue ( final UserSession session, final Variant value )
    {
        final FutureTask<WriteResult> task = new FutureTask<WriteResult> ( new Callable<WriteResult> () {

            public WriteResult call () throws Exception
            {
                processWriteCalculatedValue ( value );
                return new WriteResult ();
            }
        } );
        this.executor.execute ( task );
        return task;
    }

    protected void processWriteCalculatedValue ( final Variant value ) throws NotConvertableException, InvalidOperationException
    {
        try
        {
            this.writeHandler.write ( this.getInformation ().getName (), value );
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
