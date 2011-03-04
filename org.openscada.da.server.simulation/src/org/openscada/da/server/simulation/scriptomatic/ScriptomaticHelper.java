/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.simulation.scriptomatic;

import org.openscada.core.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptomaticHelper
{
    private final static Logger logger = LoggerFactory.getLogger ( ScriptomaticHelper.class );

    private final Hive hive;

    public ScriptomaticHelper ( final Hive hive )
    {
        this.hive = hive;
    }

    public ScriptomaticItem getItem ( final String itemId )
    {
        return this.hive.getItem ( itemId );
    }

    public void updateData ( final ScriptomaticItem item, final Variant value )
    {
        logger.warn ( "Updating value: {}", value );
        item.updateData ( value, null, null );
    }

    public void updateDataLong ( final ScriptomaticItem item, final Long value )
    {
        updateData ( item, new Variant ( value ) );
    }

    public void updateDataBoolean ( final ScriptomaticItem item, final boolean value )
    {
        updateData ( item, new Variant ( value ) );
    }

    public void updateDataString ( final ScriptomaticItem item, final String value )
    {
        updateData ( item, new Variant ( value ) );
    }

    public void updateDataDouble ( final ScriptomaticItem item, final Double value )
    {
        updateData ( item, new Variant ( value ) );
    }
}
