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
import java.util.regex.Pattern;

import org.openscada.core.Variant;

public class FactoryTemplate
{
    private Pattern _pattern = null;

    private Map<String, Variant> _itemAttributes = new HashMap<String, Variant> ();

    private Map<String, Variant> _browserAttributes = new HashMap<String, Variant> ();

    private List<ChainEntry> _chainEntries = new LinkedList<ChainEntry> ();

    public FactoryTemplate ()
    {
        super ();
    }

    public FactoryTemplate ( final FactoryTemplate arg0 )
    {
        super ();

        this._pattern = arg0._pattern;
        this._itemAttributes = new HashMap<String, Variant> ( arg0._itemAttributes );
        this._browserAttributes = new HashMap<String, Variant> ( arg0._browserAttributes );
        this._chainEntries = new LinkedList<ChainEntry> ( arg0._chainEntries );
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

    public Pattern getPattern ()
    {
        return this._pattern;
    }

    public void setPattern ( final Pattern pattern )
    {
        this._pattern = pattern;
    }

    public List<ChainEntry> getChainEntries ()
    {
        return this._chainEntries;
    }

    public void setChainEntries ( final List<ChainEntry> chainEntries )
    {
        this._chainEntries = chainEntries;
    }
}
