/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.chain.item;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.BaseChainItemCommon;

/**
 * This chain item eliminates the error flag if a manual value is currently active
 * @author Jens Reimann
 *
 */
public class ManualErrorOverrideChainItem extends BaseChainItemCommon
{
    public ManualErrorOverrideChainItem ()
    {
        super ( null );
    }

    protected void checkAndReplace ( final Map<String, Variant> attributes, final String name, final Variant replacement )
    {
        if ( attributes.containsKey ( name ) )
        {
            final Variant originalValue = attributes.get ( name );
            attributes.put ( ManualOverrideChainItem.MANUAL_BASE + "." + name + ".original", originalValue );
            attributes.put ( name, replacement );
        }
    }

    public Variant process ( final Variant value, final Map<String, Variant> attributes )
    {
        final Variant active = attributes.get ( ManualOverrideChainItem.MANUAL_ACTIVE );
        if ( active != null && active.asBoolean () )
        {
            checkAndReplace ( attributes, "error", new Variant ( false ) );
            checkAndReplace ( attributes, "error.count", new Variant ( 0 ) );
            checkAndReplace ( attributes, "error.items", new Variant ( "" ) );
        }

        return null;
    }

}
