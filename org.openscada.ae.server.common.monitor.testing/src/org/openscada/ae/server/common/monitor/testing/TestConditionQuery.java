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

package org.openscada.ae.server.common.monitor.testing;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openscada.ae.MonitorStatus;
import org.openscada.ae.MonitorStatusInformation;
import org.openscada.ae.server.common.monitor.MonitorQuery;
import org.openscada.core.Variant;

public class TestConditionQuery extends MonitorQuery
{
    private final ScheduledThreadPoolExecutor scheduler;

    private static final Random r = new Random ();

    public TestConditionQuery ( final Executor executor )
    {
        super ( executor );
        this.scheduler = new ScheduledThreadPoolExecutor ( 1 );
        this.scheduler.scheduleAtFixedRate ( new Runnable () {

            @Override
            public void run ()
            {
                tick ();
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS );
    }

    protected void tick ()
    {
        updateData ( new MonitorStatusInformation[] { new MonitorStatusInformation ( "test", r.nextBoolean () ? MonitorStatus.OK : MonitorStatus.NOT_OK, new Date (), Variant.NULL, new Date (), "system", null, null ) }, null );
    }

    public void stop ()
    {
        this.scheduler.shutdown ();
    }
}
