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

package org.openscada.ca;

import org.eclipse.scada.utils.lang.Immutable;
import org.openscada.ca.data.FactoryState;

@Immutable
public class FactoryEvent
{

    public static enum Type
    {
        STATE,
        ADDED,
        REMOVED,
    }

    private final Factory factory;

    private final Type type;

    private final FactoryState state;

    public FactoryEvent ( final Type type, final Factory factory, final FactoryState state )
    {
        this.type = type;
        this.factory = factory;
        this.state = state;
    }

    public FactoryState getState ()
    {
        return this.state;
    }

    public Factory getFactory ()
    {
        return this.factory;
    }

    public Type getType ()
    {
        return this.type;
    }

    @Override
    public String toString ()
    {
        switch ( this.type )
        {
            case STATE:
                return String.format ( "%s -> %s : %s", this.factory.getId (), this.type, this.state );
            default:
                return String.format ( "%s -> %s", this.factory.getId (), this.type );
        }

    }
}
