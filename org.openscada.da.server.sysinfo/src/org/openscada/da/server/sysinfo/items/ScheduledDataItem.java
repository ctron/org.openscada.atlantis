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

package org.openscada.da.server.sysinfo.items;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openscada.da.server.common.DataItemInputCommon;
import org.openscada.da.server.common.SuspendableDataItem;

public abstract class ScheduledDataItem extends DataItemInputCommon implements Runnable, SuspendableDataItem
{

    private final ScheduledExecutorService scheduler;

    private final int period;

    private ScheduledFuture<?> future;

    public ScheduledDataItem ( final String name, final ScheduledExecutorService scheduler, final int period )
    {
        super ( name );
        this.period = period;
        this.scheduler = scheduler;
    }

    public void suspend ()
    {
        this.future.cancel ( false );
    }

    public void wakeup ()
    {
        this.future = this.scheduler.scheduleAtFixedRate ( this, 0, this.period, TimeUnit.MILLISECONDS );
    }

}
