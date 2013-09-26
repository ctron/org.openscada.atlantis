/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.eclipse.scada.core.client.ngp;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.scada.core.info.StatisticsImpl;
import org.eclipse.scada.sec.callback.CallbackHandler;

public class CallbackHandlerManager
{
    private static final Object STAT_REGISTERED_CALLBACK_HANDLERS = new Object ();

    private final Map<Long, CallbackHandler> map = new HashMap<Long, CallbackHandler> ();

    private final StatisticsImpl statistics;

    public CallbackHandlerManager ( final StatisticsImpl statistics )
    {
        this.statistics = statistics;
        this.statistics.setLabel ( STAT_REGISTERED_CALLBACK_HANDLERS, "Registered callback handlers" );
    }

    public void registerHandler ( final long handlerId, final CallbackHandler callbackHandler )
    {
        this.map.put ( handlerId, callbackHandler );
        this.statistics.setCurrentValue ( STAT_REGISTERED_CALLBACK_HANDLERS, this.map.size () );
    }

    public void unregisterHandler ( final long handlerId )
    {
        this.map.remove ( handlerId );
        this.statistics.setCurrentValue ( STAT_REGISTERED_CALLBACK_HANDLERS, this.map.size () );
    }

    public CallbackHandler getHandler ( final long handlerId )
    {
        return this.map.get ( handlerId );
    }

}
