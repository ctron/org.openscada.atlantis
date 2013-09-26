/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.common.chain;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.eclipse.scada.core.AttributesHelper;
import org.eclipse.scada.core.InvalidOperationException;
import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.data.IODirection;
import org.eclipse.scada.utils.concurrent.InstantErrorFuture;
import org.eclipse.scada.utils.concurrent.InstantFuture;
import org.eclipse.scada.utils.concurrent.NotifyFuture;
import org.openscada.core.server.OperationParameters;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.chain.item.AutoTimestampChainItem;

public class DataItemInputChained extends DataItemBaseChained
{
    protected Variant primaryValue = Variant.NULL;

    protected Variant secondaryValue = Variant.NULL;

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
     * 
     * @param value
     *            the new value, or <code>null</code> if the value did not
     *            change
     * @param attributes
     *            the new attributes, <code>null</code> if no attribute have
     *            changed
     * @param mode
     *            The attribute change mode, <code>null</code> will use the
     *            default ( {@link AttributeMode#UPDATE} )
     */
    public synchronized void updateData ( final Variant value, final Map<String, Variant> attributes, AttributeMode mode )
    {
        boolean changed = false;

        // handle value change
        if ( value != null && !this.primaryValue.equals ( value ) )
        {
            this.primaryValue = Variant.valueOf ( value );
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
        Variant newSecondaryValue = Variant.valueOf ( this.primaryValue );
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
            newValue = this.secondaryValue = Variant.valueOf ( newSecondaryValue );
        }

        this.secondaryAttributes.set ( newValue, newAttributes );
    }

    @Override
    public NotifyFuture<Variant> readValue () throws InvalidOperationException
    {
        return new InstantFuture<Variant> ( this.secondaryValue );
    }

    @Override
    public NotifyFuture<WriteResult> startWriteValue ( final Variant value, final OperationParameters operationParameters )
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
