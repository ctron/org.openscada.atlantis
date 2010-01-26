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

package org.openscada.da.server.common;

import java.util.EnumSet;
import java.util.Map;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public class MemoryDataItem extends DataItemBase
{
    private volatile Variant value = new Variant ();

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

    public NotifyFuture<Variant> readValue () throws InvalidOperationException
    {
        return new InstantFuture<Variant> ( this.value );
    }

    public NotifyFuture<WriteResult> startWriteValue ( final Variant value )
    {
        if ( !this.value.equals ( value ) )
        {
            this.value = new Variant ( value );
            notifyData ( value, null );
        }

        // we can handle this directly
        return new InstantFuture<WriteResult> ( new WriteResult () );
    }

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

    public NotifyFuture<WriteAttributeResults> startSetAttributes ( final Map<String, Variant> attributes )
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
