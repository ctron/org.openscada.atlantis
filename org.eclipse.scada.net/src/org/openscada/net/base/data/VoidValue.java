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

package org.openscada.net.base.data;

import org.eclipse.scada.utils.lang.Immutable;

/**
 * A void value type.
 * <p>
 * A void instance is equal to all other void instances since they don't have a real value.
 * 
 * @author Jens Reimann
 */
@Immutable
public class VoidValue extends Value
{
    public final static VoidValue INSTANCE = new VoidValue ();

    protected VoidValue ()
    {
    }

    @Override
    public int hashCode ()
    {
        return 31;
    }

    @Override
    public boolean equals ( final Object other )
    {
        if ( this == other )
        {
            return true;
        }
        if ( other == null )
        {
            return false;
        }
        if ( getClass () != other.getClass () )
        {
            return false;
        }
        return true;
    }

}
