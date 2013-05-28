/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

import java.util.Map;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.Variant;
import org.openscada.core.server.OperationParameters;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public class DataItemInputCommon extends DataItemInput
{
    private volatile Variant value = Variant.NULL;

    private AttributeManager attributes = null;

    public DataItemInputCommon ( final DataItemInformation info )
    {
        super ( info );
        this.attributes = new AttributeManager ( this );
    }

    public DataItemInputCommon ( final String name )
    {
        super ( name );
        this.attributes = new AttributeManager ( this );
    }

    @Override
    public NotifyFuture<Variant> readValue () throws InvalidOperationException
    {
        return new InstantFuture<Variant> ( this.value );
    }

    @Override
    public Map<String, Variant> getAttributes ()
    {
        return this.attributes.get ();
    }

    /**
     * Perform requests from the hive to update the items attributes <br>
     * This method actually. Reacting to attribute set requests is
     * implementation dependent. So you will need to subclass from
     * DataItemInputCommon and override this method. <br>
     * If you simple need a memory container that simply stores what you write
     * into it consider using the MemoryDataItem. <br>
     * If you are implementing a data item based on this item and wish to change
     * the data items attributes use {@link #getAttributeManager()} to get the
     * attribute manager which allows you so tweak the
     * items attributes from the side of the item implementation.
     */
    @Override
    public NotifyFuture<WriteAttributeResults> startSetAttributes ( final Map<String, Variant> attributes, final OperationParameters operationParameters )
    {
        return new InstantFuture<WriteAttributeResults> ( WriteAttributesHelper.errorUnhandled ( null, attributes ) );
    }

    /**
     * Update the value of this data item
     * 
     * @param value
     *            the new value
     */
    public synchronized void updateData ( Variant value, final Map<String, Variant> attributes, final AttributeMode mode )
    {
        if ( this.value == null || !this.value.equals ( value ) )
        {
            this.value = value;
        }
        else
        {
            value = null;
        }

        this.attributes.update ( value, attributes, mode );
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
}
