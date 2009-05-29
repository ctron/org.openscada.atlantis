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

package org.openscada.da.server.simulation.filesource;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.VariantType;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class ItemDefinition
{
    private final Map<String, Variant> attributes = new HashMap<String, Variant> ();

    private String callback = "function (item) { logger.info('callback called with ' + item.getInformation().getName()); }";

    private Object defaultValue = null;

    private String description = "";

    private Direction direction = Direction.INOUT;

    private String name;

    private VariantType type = VariantType.NULL;

    private String unit = "";

    private String writeHandler = "function (item, value) { item.updateData(value, null, org.openscada.da.server.common.AttributeMode.UPDATE); }";

    public ItemDefinition addAttr ( final String name, final Object value )
    {
        this.attributes.put ( name, new Variant ( value ) );
        return this;
    }

    public Map<String, Variant> getAttributes ()
    {
        return this.attributes;
    }

    public String getCallback ()
    {
        return this.callback;
    }

    public Object getDefaultValue ()
    {
        return this.defaultValue;
    }

    public String getDescription ()
    {
        return this.description;
    }

    public Direction getDirection ()
    {
        return this.direction;
    }

    public String getName ()
    {
        return this.name;
    }

    public VariantType getType ()
    {
        return this.type;
    }

    public String getUnit ()
    {
        return this.unit;
    }

    public String getWriteHandler ()
    {
        return this.writeHandler;
    }

    public void setCallback ( final String callback )
    {
        this.callback = callback;
    }

    public void setDefaultValue ( final Object defaultValue )
    {
        this.defaultValue = defaultValue;
    }

    public void setDescription ( final String description )
    {
        this.description = description;
    }

    public void setDirection ( final Direction direction )
    {
        this.direction = direction;
    }

    public void setName ( final String name )
    {
        this.name = name;
    }

    public void setType ( final VariantType type )
    {
        this.type = type;
    }

    public void setUnit ( final String unit )
    {
        this.unit = unit;
    }

    public void setWriteHandler ( final String writeHandler )
    {
        this.writeHandler = writeHandler;
    }
}
