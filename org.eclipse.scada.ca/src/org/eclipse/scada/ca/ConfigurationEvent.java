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

package org.eclipse.scada.ca;

import org.eclipse.scada.ca.data.ConfigurationState;

public class ConfigurationEvent
{
    public static enum Type
    {
        CREATED,
        MODIFIED,
        REMOVED,
        STATE
    }

    private final Configuration configuration;

    private final Type type;

    private final ConfigurationState state;

    private final Throwable error;

    public ConfigurationEvent ( final Type type, final Configuration configuration, final ConfigurationState state, final Throwable error )
    {
        this.type = type;
        this.configuration = configuration;
        this.state = state;
        this.error = error;
    }

    public ConfigurationState getState ()
    {
        return this.state;
    }

    public Throwable getError ()
    {
        return this.error;
    }

    public Configuration getConfiguration ()
    {
        return this.configuration;
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
                return String.format ( "%s -> %s / %s", this.configuration.getId (), this.type, this.state );
            default:
                return String.format ( "%s -> %s / %s", this.configuration.getId (), this.type, this.configuration );
        }

    }
}
