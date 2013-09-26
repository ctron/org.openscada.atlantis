/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.ae.monitor.common.testing;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.scada.ae.data.Severity;
import org.eclipse.scada.core.Variant;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.common.AbstractStateMonitor;
import org.openscada.ae.monitor.common.PersistentInformation;
import org.openscada.ae.server.common.akn.AknHandler;
import org.openscada.core.server.OperationParameters;
import org.osgi.framework.BundleContext;

public class TestingMonitor extends AbstractStateMonitor implements AknHandler
{

    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor ( 1 );

    private final Random r = new Random ();

    public TestingMonitor ( final BundleContext context, final Executor executor, final EventProcessor eventProcessor, final String sourceName )
    {
        super ( sourceName, executor, null, eventProcessor );
        this.scheduler.scheduleAtFixedRate ( new Runnable () {

            @Override
            public void run ()
            {
                TestingMonitor.this.tick ();
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS );
    }

    protected void tick ()
    {
        if ( this.r.nextBoolean () )
        {
            setOk ( Variant.TRUE, System.currentTimeMillis () );
        }
        else
        {
            setFailure ( Variant.FALSE, System.currentTimeMillis (), Severity.ALARM, false );
        }
    }

    public void stop ()
    {
        this.scheduler.shutdown ();
    }

    @Override
    public boolean acknowledge ( final String conditionId, final OperationParameters operationParameters, final Date aknTimestamp )
    {
        if ( getId ().equals ( conditionId ) )
        {
            akn ( operationParameters == null ? null : operationParameters.getUserInformation (), aknTimestamp );
            return true;
        }
        return false;
    }

    @Override
    protected void storePersistentInformation ( final PersistentInformation persistentInformation )
    {
        // no-op
    }

}
