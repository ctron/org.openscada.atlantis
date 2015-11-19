/*******************************************************************************
 * Copyright (c) 2015 IBH SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBH SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.openscada.da.server.opc.xmlda;

import java.util.Collections;
import java.util.concurrent.Executor;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.server.OperationParameters;
import org.eclipse.scada.da.core.WriteResult;
import org.eclipse.scada.da.server.common.AttributeMode;
import org.eclipse.scada.da.server.common.SuspendableDataItem;
import org.eclipse.scada.da.server.common.chain.DataItemInputOutputChained;
import org.eclipse.scada.utils.concurrent.NotifyFuture;
import org.eclipse.scada.utils.concurrent.TransformResultFuture;
import org.openscada.opc.xmlda.Connection;
import org.openscada.opc.xmlda.ItemRequest;
import org.openscada.opc.xmlda.Poller;
import org.openscada.opc.xmlda.requests.ItemValue;
import org.openscada.opc.xmlda.requests.WriteRequest;
import org.openscada.opc.xmlda.requests.WriteResponse;

public class RemoteDataItem extends DataItemInputOutputChained implements SuspendableDataItem
{
    private final String id;

    private final Connection connection;

    private final String itemName;

    private final String itemPath;

    private final Poller poller;

    final ItemRequest itemRequest;

    public RemoteDataItem ( final String id, final Executor executor, final Connection connection, final Poller poller, final String itemName, final String itemPath )
    {
        super ( id, executor );

        this.id = id;
        this.connection = connection;
        this.poller = poller;
        this.itemName = itemName;
        this.itemPath = itemPath;

        this.itemRequest = new ItemRequest ( this.id, this.itemName, this.itemPath );
    }

    @Override
    public synchronized void suspend ()
    {
        this.poller.removeItem ( this.itemRequest );
    }

    @Override
    public void wakeup ()
    {
        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                interalWakeup ();
            }
        } );
    }

    protected synchronized void interalWakeup ()
    {
        updateData ( Variant.NULL, Collections.singletonMap ( "opcxmlda.init.error", Variant.TRUE ), AttributeMode.SET );
        this.poller.addItem ( this.itemRequest );
    }

    @Override
    protected NotifyFuture<WriteResult> startWriteCalculatedValue ( final Variant value, final OperationParameters operationParameters )
    {
        final NotifyFuture<WriteResponse> future = this.connection.scheduleTask ( new WriteRequest ( new ItemValue ( this.itemName, this.itemPath, value.getValue (), null, null, null ) ) );

        return new TransformResultFuture<WriteResponse, WriteResult> ( future) {

            @Override
            protected WriteResult transform ( final WriteResponse from ) throws Exception
            {
                return WriteResult.OK; // TODO: check if WriteResponse can provide more information
            }
        };
    }
}
