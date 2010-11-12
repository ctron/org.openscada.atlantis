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

import java.util.EnumSet;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.Variant;
import org.openscada.core.server.common.session.UserSession;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.chain.MemoryItemChained;
import org.openscada.da.server.common.chain.item.ChainCreator;
import org.openscada.da.server.spring.Hive;
import org.openscada.da.server.spring.TestErrorChainItem;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public class CSVDataItem extends MemoryItemChained
{

    protected CSVControllerDataItem controllerItem;

    public CSVDataItem ( final Hive hive, final String name, final EnumSet<IODirection> ioDirection )
    {
        super ( new DataItemInformationBase ( name, ioDirection ) );
        this.addChainElement ( IODirection.INPUT, new TestErrorChainItem () );
        ChainCreator.applyDefaultInputChain ( this, hive );
    }

    @Override
    public NotifyFuture<Variant> readValue () throws InvalidOperationException
    {
        if ( !isReadable () )
        {
            throw new InvalidOperationException ();
        }
        return super.readValue ();
    }

    @Override
    protected NotifyFuture<WriteResult> startWriteCalculatedValue ( final UserSession session, final Variant value )
    {
        fireWrite ( value );
        if ( isReadable () )
        {
            return super.startWriteCalculatedValue ( session, value );
        }
        return new InstantErrorFuture<WriteResult> ( new InvalidOperationException ().fillInStackTrace () );
    }

    @Override
    public NotifyFuture<WriteResult> startWriteValue ( final UserSession session, final Variant value )
    {
        if ( !isWriteable () )
        {
            return new InstantErrorFuture<WriteResult> ( new InvalidOperationException ().fillInStackTrace () );
        }
        return super.startWriteValue ( session, value );
    }

    private void fireWrite ( final Variant value )
    {
        final CSVControllerDataItem controllerItem = this.controllerItem;
        if ( controllerItem != null )
        {
            controllerItem.handleWrite ( value );
        }
    }

    public void setController ( final CSVControllerDataItem controllerItem )
    {
        this.controllerItem = controllerItem;
    }

    public boolean isReadable ()
    {
        return this.getInformation ().getIODirection ().contains ( IODirection.INPUT );
    }

    public boolean isWriteable ()
    {
        return this.getInformation ().getIODirection ().contains ( IODirection.OUTPUT );
    }

}
