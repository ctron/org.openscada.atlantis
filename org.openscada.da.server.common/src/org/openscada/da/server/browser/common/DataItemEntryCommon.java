/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.browser.common;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.data.IODirection;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.browser.DataItemEntry;

public class DataItemEntryCommon implements DataItemEntry
{

    private final String name;

    private final DataItemInformation itemInformation;

    private final Map<String, Variant> attributes;

    public DataItemEntryCommon ( final String name, final DataItemInformation itemInformation, final Map<String, Variant> attributes )
    {
        this.name = name;
        this.itemInformation = itemInformation;

        if ( attributes == null )
        {
            this.attributes = Collections.emptyMap ();
        }
        else
        {
            this.attributes = attributes;
        }
    }

    @Override
    public String getId ()
    {
        return this.itemInformation.getName ();
    }

    @Override
    public String getName ()
    {
        return this.name;
    }

    @Override
    public Map<String, Variant> getAttributes ()
    {
        return this.attributes;
    }

    @Override
    public Set<IODirection> getIODirections ()
    {
        return Collections.unmodifiableSet ( this.itemInformation.getIODirection () );
    }

}
