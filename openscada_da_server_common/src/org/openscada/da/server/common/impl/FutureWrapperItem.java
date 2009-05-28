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

package org.openscada.da.server.common.impl;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.FutureDataItem;
import org.openscada.da.server.common.ItemListener;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.NotifyFuture;

/**
 * A data item which wraps a normal data item to implement {@link FutureDataItem}
 * @author Jens Reimann
 *
 */
public class FutureWrapperItem implements FutureDataItem
{

    private final DataItem item;

    private final Executor executor;

    public FutureWrapperItem ( final DataItem item, final Executor executor )
    {
        this.item = item;
        this.executor = executor;
    }

    public NotifyFuture<WriteAttributeResults> startSetAttributes ( final Map<String, Variant> attributes )
    {
        final FutureTask<WriteAttributeResults> task = new FutureTask<WriteAttributeResults> ( new Callable<WriteAttributeResults> () {

            public WriteAttributeResults call () throws Exception
            {
                return FutureWrapperItem.this.item.setAttributes ( attributes );
            }
        } );
        this.executor.execute ( task );
        return task;
    }

    public NotifyFuture<WriteResult> startWriteValue ( final Variant value )
    {
        final FutureTask<WriteResult> task = new FutureTask<WriteResult> ( new Callable<WriteResult> () {

            public WriteResult call () throws Exception
            {
                FutureWrapperItem.this.item.writeValue ( value );
                return new WriteResult ();
            }
        } );
        this.executor.execute ( task );
        return task;
    }

    public Map<String, Variant> getAttributes ()
    {
        return this.item.getAttributes ();
    }

    public DataItemInformation getInformation ()
    {
        return this.item.getInformation ();
    }

    public NotifyFuture<Variant> readValue () throws InvalidOperationException
    {
        return this.item.readValue ();
    }

    public WriteAttributeResults setAttributes ( final Map<String, Variant> attributes )
    {
        return this.item.setAttributes ( attributes );
    }

    public void setListener ( final ItemListener listener )
    {
        this.item.setListener ( listener );
    }

    public void writeValue ( final Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException, OperationException
    {
        this.item.writeValue ( value );
    }

}
