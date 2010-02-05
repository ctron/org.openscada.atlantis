/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2007-2009 inavare GmbH (http://inavare.com)
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
