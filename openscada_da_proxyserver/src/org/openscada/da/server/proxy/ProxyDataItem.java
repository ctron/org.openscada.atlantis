/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.proxy;

import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.NoConnectionException;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class ProxyDataItem extends DataItemInputOutputChained
{
    private final ProxyValueHolder proxyValueHolder;

    /**
     * @param id
     * @param proxyValueHolder
     */
    public ProxyDataItem ( final String id, final ProxyValueHolder proxyValueHolder )
    {
        super ( new DataItemInformationBase ( id, EnumSet.allOf ( IODirection.class ) ) );
        this.proxyValueHolder = proxyValueHolder;
        this.proxyValueHolder.setListener ( new ItemUpdateListener () {
            @Override
            public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
            {
                ProxyDataItem.this.updateData ( value, attributes, cache ? AttributeMode.SET : AttributeMode.UPDATE );
            }

            @Override
            public void notifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
            {
                // TODO: (jr2) is there something which is to be done?
            }
        } );
    }

    /**
     * @return object which holds the actual data
     */
    public ProxyValueHolder getProxyValueHolder ()
    {
        return this.proxyValueHolder;
    }

    @Override
    public WriteAttributeResults setAttributes ( final Map<String, Variant> attributes )
    {
        final WriteAttributeResults writeAttributeResults = super.setAttributes ( attributes );
        // all attributes which could be successfully processed by chain must be ignored
        for ( final Entry<String, WriteAttributeResult> entry : writeAttributeResults.entrySet () )
        {
            if ( entry.getValue ().isSuccess () )
            {
                attributes.remove ( entry.getKey () );
            }
        }
        this.proxyValueHolder.writeAttributes ( this.getInformation ().getName (), attributes, writeAttributeResults );
        return writeAttributeResults;
    }

    /**
     * @param attributes
     */
    public void setTemplateAttributes ( final Map<String, Variant> attributes )
    {
        super.setAttributes ( attributes );
    }

    @Override
    protected void writeCalculatedValue ( final Variant value ) throws NotConvertableException, InvalidOperationException
    {
        try
        {
            this.proxyValueHolder.write ( this.getInformation ().getName (), value );
        }
        catch ( final NoConnectionException e )
        {
            throw new InvalidOperationException ();
        }
        catch ( final OperationException e )
        {
            throw new InvalidOperationException ();
        }
    }
}
