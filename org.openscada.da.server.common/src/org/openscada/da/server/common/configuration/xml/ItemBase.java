/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.da.server.common.configuration.xml;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.factory.ChainEntry;

public class ItemBase
{

    private Factory _factory = null;

    private Map<String, Variant> _itemAttributes = new HashMap<String, Variant> ();

    private Map<String, Variant> _browserAttributes = new HashMap<String, Variant> ();

    private List<ChainEntry> _chainEntries = new LinkedList<ChainEntry> ();

    public ItemBase ()
    {
        super ();
    }

    public ItemBase ( final ItemBase arg0 )
    {
        super ();

        this._factory = arg0._factory;
        this._chainEntries = new LinkedList<ChainEntry> ( arg0._chainEntries );

        this._itemAttributes = new HashMap<String, Variant> ( arg0._itemAttributes );
        this._browserAttributes = new HashMap<String, Variant> ( arg0._browserAttributes );
    }

    public Map<String, Variant> getBrowserAttributes ()
    {
        return this._browserAttributes;
    }

    public void setBrowserAttributes ( final Map<String, Variant> browserAttributes )
    {
        this._browserAttributes = browserAttributes;
    }

    public Map<String, Variant> getItemAttributes ()
    {
        return this._itemAttributes;
    }

    public void setItemAttributes ( final Map<String, Variant> itemAttributes )
    {
        this._itemAttributes = itemAttributes;
    }

    public Factory getFactory ()
    {
        return this._factory;
    }

    public void setFactory ( final Factory factory )
    {
        this._factory = factory;
    }

    public List<ChainEntry> getChainEntries ()
    {
        return this._chainEntries;
    }

    public void setChainEntries ( final List<ChainEntry> chainItems )
    {
        this._chainEntries = chainItems;
    }

}