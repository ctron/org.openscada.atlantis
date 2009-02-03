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

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.NoConnectionException;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.server.common.DataItemBase;
import org.openscada.da.server.common.DataItemInformationBase;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class ProxyDataItem extends DataItemBase
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
                ProxyDataItem.this.notifyData ( value, attributes, cache );
            }

            @Override
            public void notifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
            {
                // TODO: (jr) is there something which is to be done?
            }
        } );
    }

    @Override
    protected Map<String, Variant> getCacheAttributes ()
    {
        return this.proxyValueHolder.getAttributes ();
    }

    @Override
    protected Variant getCacheValue ()
    {
        return this.proxyValueHolder.getValue ();
    }

    @Override
    public Map<String, Variant> getAttributes ()
    {
        return this.proxyValueHolder.getAttributes ();
    }

    /**
     * @return object which holds the actual data
     */
    public ProxyValueHolder getProxyValueHolder ()
    {
        return this.proxyValueHolder;
    }

    @Override
    public Variant readValue () throws InvalidOperationException
    {
        return this.proxyValueHolder.getValue ();
    }

    @Override
    public WriteAttributeResults setAttributes ( final Map<String, Variant> attributes )
    {
        try
        {
            return this.proxyValueHolder.writeAttributes ( this.getInformation ().getName (), attributes );
        }
        catch ( final NoConnectionException e )
        {
            return attributesCouldNotBeWritten ( attributes, e );
        }
        catch ( final OperationException e )
        {
            return attributesCouldNotBeWritten ( attributes, e );
        }
    }

    /**
     * creates a WriteAttributeResults object for given attributes filled 
     * with given exception for each attribute
     * @param attributes
     * @param e
     * @return
     */
    private WriteAttributeResults attributesCouldNotBeWritten ( final Map<String, Variant> attributes, final Exception e )
    {
        final WriteAttributeResults results = new WriteAttributeResults ();
        for ( final String name : attributes.keySet () )
        {
            results.put ( name, new WriteAttributeResult ( e ) );
        }
        return results;
    }

    @Override
    public void writeValue ( final Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
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
