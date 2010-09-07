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
            checkAndReplace ( attributes, "error", Variant.FALSE );
            checkAndReplace ( attributes, "error.count", new Variant ( 0 ) );
            checkAndReplace ( attributes, "error.items", new Variant ( "" ) );
        }

        return null;
    }

}
