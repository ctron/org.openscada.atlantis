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

package org.openscada.da.server.common;

import java.util.EnumSet;
import java.util.Map;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public class MemoryDataItem extends DataItemBase
{
    private volatile Variant value = Variant.NULL;

    private AttributeManager attributes = null;

    public MemoryDataItem ( final String name )
    {
        this ( name, EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) );
    }

    protected MemoryDataItem ( final String name, final EnumSet<IODirection> ioDirection )
    {
        super ( new DataItemInformationBase ( name, ioDirection ) );
        this.attributes = new AttributeManager ( this );
    }

    @Override
    public NotifyFuture<Variant> readValue () throws InvalidOperationException
    {
        return new InstantFuture<Variant> ( this.value );
    }

    @Override
    public NotifyFuture<WriteResult> startWriteValue ( final Variant value, final OperationParameters operationParameters )
    {
        if ( !this.value.equals ( value ) )
        {
            this.value = value;
            notifyData ( value, null );
        }

        // we can handle this directly
        return new InstantFuture<WriteResult> ( new WriteResult () );
    }

    @Override
    public Map<String, Variant> getAttributes ()
    {
        return this.attributes.get ();
    }

    @Override
    protected Map<String, Variant> getCacheAttributes ()
    {
        return this.attributes.get ();
    }

    @Override
    protected Variant getCacheValue ()
    {
        return this.value;
    }

    @Override
    public NotifyFuture<WriteAttributeResults> startSetAttributes ( final Map<String, Variant> attributes, final OperationParameters operationParameters )
    {
        final WriteAttributeResults writeAttributeResults = new WriteAttributeResults ();

        this.attributes.update ( null, attributes );

        for ( final Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            writeAttributeResults.put ( entry.getKey (), WriteAttributeResult.OK );
        }

        return new InstantFuture<WriteAttributeResults> ( writeAttributeResults );
    }

}
