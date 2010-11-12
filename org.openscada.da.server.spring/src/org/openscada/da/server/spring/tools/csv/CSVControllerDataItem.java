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

package org.openscada.da.server.spring.tools.csv;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import org.openscada.core.Variant;
import org.openscada.core.server.common.session.UserSession;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.NotifyFuture;

public class CSVControllerDataItem extends DataItemInputOutputChained
{
    private final CSVDataItem item;

    public CSVControllerDataItem ( final CSVDataItem item, final Executor executor )
    {
        super ( item.getInformation ().getName () + "#controller", executor );
        this.item = item;
        this.item.setController ( this );
    }

    public void handleWrite ( final Variant value )
    {
        updateData ( value, null, null );
    }

    @Override
    protected NotifyFuture<WriteResult> startWriteCalculatedValue ( final UserSession session, final Variant value )
    {
        final FutureTask<WriteResult> task = new FutureTask<WriteResult> ( new Callable<WriteResult> () {

            public WriteResult call () throws Exception
            {
                performUpdate ( value );
                return new WriteResult ();
            }
        } );
        this.executor.execute ( task );
        return task;
    }

    protected void performUpdate ( final Variant value )
    {
        this.item.updateData ( value, null, null );
    }
}
