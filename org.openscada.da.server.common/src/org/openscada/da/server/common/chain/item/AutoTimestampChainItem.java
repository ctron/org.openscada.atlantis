/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
 * A chain item which will add a timestamp by default it none is provided.
 * @author Jens Reimann
 *
 */
public class AutoTimestampChainItem extends BaseChainItemCommon
{
    private Variant lastValue = Variant.NULL;

    public AutoTimestampChainItem ()
    {
        super ( null );
    }

    @Override
    public Variant process ( final Variant value, final Map<String, Variant> attributes )
    {
        if ( value == null )
        {
            // no value change
            return null;
        }

        if ( !this.lastValue.equals ( value ) )
        {
            if ( !attributes.containsKey ( "timestamp" ) )
            {
                attributes.put ( "timestamp", Variant.valueOf ( System.currentTimeMillis () ) );
            }
            this.lastValue = value;
        }
        return null;
    }

}
