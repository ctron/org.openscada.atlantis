/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.opc.connection;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.opc.dcom.da.OPCITEMDEF;

/**
 * Request for an item based on the {@link OPCITEMDEF} structure
 * @author jens
 *
 */
public class ItemRegistrationRequest
{
    private OPCITEMDEF itemDefinition;

    private Map<String, Variant> attributes;

    public OPCITEMDEF getItemDefinition ()
    {
        return this.itemDefinition;
    }

    public void setItemDefinition ( final OPCITEMDEF itemDefinition )
    {
        this.itemDefinition = itemDefinition;
    }

    public Map<String, Variant> getAttributes ()
    {
        return this.attributes;
    }

    public void setAttributes ( final Map<String, Variant> attributes )
    {
        this.attributes = attributes;
    }
}
