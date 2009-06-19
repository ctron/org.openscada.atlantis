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

package org.openscada.da.server.common.chain;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.Variant;
import org.openscada.core.utils.AttributesHelper;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.chain.item.AutoTimestampChainItem;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public class DataItemInputChained extends DataItemBaseChained
{
    protected Variant primaryValue = new Variant ();

    protected Variant secondaryValue = new Variant ();

    public DataItemInputChained ( final DataItemInformation dataItemInformation, final Executor executor )
    {
        this ( dataItemInformation, true, executor );
    }

    public DataItemInputChained ( final DataItemInformation dataItemInformation, final boolean autoTimestamp, final Executor executor )
    {
        super ( dataItemInformation, executor );
        if ( autoTimestamp )
        {
            addChainElement ( IODirection.INPUT, new AutoTimestampChainItem () );
        }
    }

    public DataItemInputChained ( final String id, final Executor executor )
    {
        this ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT ) ), true, executor );
    }

    /**
     * Update the item data
     * @param value the new value, or <code>null</code> if the value did not change
     * @param attributes the new attributes, <code>null</code> if no attribute have changed
     * @param mode The attribute change mode, <code>null</code> will use the default ( {@link AttributeMode#UPDATE} )
     */
    public synchronized void updateData ( final Variant value, final Map<String, Variant> attributes, AttributeMode mode )
    {
        boolean changed = false;

        // handle value change
        if ( value != null && !this.primaryValue.equals ( value ) )
        {
            this.primaryValue = new Variant ( value );
            changed = true;
        }

        // change attribute change
        if ( attributes != null )
        {
            if ( mode == null )
            {
                mode = AttributeMode.UPDATE;
            }

            final Map<String, Variant> diff = new HashMap<String, Variant> ();
            if ( mode == AttributeMode.SET )
            {
                AttributesHelper.set ( this.primaryAttributes, attributes, diff );
            }
            else
            {
                AttributesHelper.mergeAttributes ( this.primaryAttributes, attributes, diff );
            }
            changed = changed || !diff.isEmpty ();
        }

        if ( changed )
        {
            process ();
        }
    }

    @Override
    protected void process ()
    {
        Variant newSecondaryValue = new Variant ( this.primaryValue );
        final Map<String, Variant> newAttributes = new HashMap<String, Variant> ( this.primaryAttributes );

        for ( final ChainProcessEntry entry : getChainCopy () )
        {
            if ( entry.getWhen ().contains ( IODirection.INPUT ) )
            {
                final Variant newValue = entry.getWhat ().process ( newSecondaryValue, newAttributes );
                if ( newValue != null )
                {
                    newSecondaryValue = newValue;
                }
            }
        }

        Variant newValue = null;
        if ( !this.secondaryValue.equals ( newSecondaryValue ) )
        {
            newValue = this.secondaryValue = new Variant ( newSecondaryValue );
        }

        this.secondaryAttributes.set ( newValue, newAttributes );
    }

    public NotifyFuture<Variant> readValue () throws InvalidOperationException
    {
        return new InstantFuture<Variant> ( this.secondaryValue );
    }

    public NotifyFuture<WriteResult> startWriteValue ( final Variant value )
    {
        return new InstantErrorFuture<WriteResult> ( new InvalidOperationException () );
    }

    @Override
    protected Map<String, Variant> getCacheAttributes ()
    {
        return this.secondaryAttributes.get ();
    }

    @Override
    protected Variant getCacheValue ()
    {
        return this.secondaryValue;
    }

}
