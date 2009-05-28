/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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
import java.util.concurrent.Future;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.utils.AttributesHelper;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.utils.concurrent.InstantFuture;

public class DataItemInputChained extends DataItemBaseChained
{
    protected Variant _primaryValue = new Variant ();

    protected Variant _secondaryValue = new Variant ();

    public DataItemInputChained ( final DataItemInformation dataItemInformation )
    {
        this ( dataItemInformation, true );
    }

    public DataItemInputChained ( final DataItemInformation dataItemInformation, final boolean autoTimestamp )
    {
        super ( dataItemInformation );
        if ( autoTimestamp )
        {
            addChainElement ( IODirection.INPUT, new AutoTimestampChainItem () );
        }
    }

    public DataItemInputChained ( final String id )
    {
        this ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT ) ), true );
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
        if ( value != null && !this._primaryValue.equals ( value ) )
        {
            this._primaryValue = new Variant ( value );
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
                AttributesHelper.set ( this._primaryAttributes, attributes, diff );
            }
            else
            {
                AttributesHelper.mergeAttributes ( this._primaryAttributes, attributes, diff );
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
        final Variant newSecondaryValue = new Variant ( this._primaryValue );
        final Map<String, Variant> primaryAttributes = new HashMap<String, Variant> ( this._primaryAttributes );

        for ( final ChainProcessEntry entry : getChainCopy () )
        {
            if ( entry.getWhen ().contains ( IODirection.INPUT ) )
            {
                entry.getWhat ().process ( newSecondaryValue, primaryAttributes );
            }
        }

        Variant newValue = null;
        if ( !this._secondaryValue.equals ( newSecondaryValue ) )
        {
            newValue = this._secondaryValue = new Variant ( newSecondaryValue );
        }

        this._secondaryAttributes.set ( newValue, primaryAttributes );
    }

    public Future<Variant> readValue () throws InvalidOperationException
    {
        return new InstantFuture<Variant> ( this._secondaryValue );
    }

    public void writeValue ( final Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException, OperationException
    {
        throw new InvalidOperationException ();
    }

    @Override
    protected Map<String, Variant> getCacheAttributes ()
    {
        return this._secondaryAttributes.get ();
    }

    @Override
    protected Variant getCacheValue ()
    {
        return this._secondaryValue;
    }

}
