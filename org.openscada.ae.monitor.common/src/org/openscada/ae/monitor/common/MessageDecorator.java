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

package org.openscada.ae.monitor.common;

import java.util.Map;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.core.Variant;
import org.openscada.utils.lang.Immutable;

@Immutable
public class MessageDecorator implements MonitorDecorator
{
    private final Variant message;

    public MessageDecorator ( final Variant message )
    {
        this.message = message;
    }

    @Override
    public void decorateEvent ( final EventBuilder builder )
    {
        if ( this.message != null )
        {
            builder.attribute ( Event.Fields.MESSAGE, this.message );
        }
    }

    @Override
    public void decorateMonitorAttributes ( final Map<String, Variant> attributes )
    {
        if ( this.message != null )
        {
            attributes.put ( Event.Fields.MESSAGE.getName (), this.message );
        }
    }

}
