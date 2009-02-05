/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.factory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.ChainProcessEntry;

public class DataItemFactoryRequest
{
    private String _id = null;

    private Map<String, Variant> _itemAttributes = new HashMap<String, Variant> ();

    private Map<String, Variant> _browserAttributes = new HashMap<String, Variant> ();

    private List<ChainProcessEntry> _itemChain = new LinkedList<ChainProcessEntry> ();

    public Map<String, Variant> getBrowserAttributes ()
    {
        return this._browserAttributes;
    }

    public void setBrowserAttributes ( final Map<String, Variant> browserAttributes )
    {
        this._browserAttributes = browserAttributes;
    }

    public String getId ()
    {
        return this._id;
    }

    public void setId ( final String id )
    {
        this._id = id;
    }

    public Map<String, Variant> getItemAttributes ()
    {
        return this._itemAttributes;
    }

    public void setItemAttributes ( final Map<String, Variant> itemAttributes )
    {
        this._itemAttributes = itemAttributes;
    }

    public List<ChainProcessEntry> getItemChain ()
    {
        return this._itemChain;
    }

    public void setItemChain ( final List<ChainProcessEntry> itemChain )
    {
        this._itemChain = itemChain;
    }

}
