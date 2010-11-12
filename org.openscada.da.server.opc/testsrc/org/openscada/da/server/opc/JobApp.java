/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.opc;

import java.util.Random;

import org.apache.log4j.Logger;
import org.openscada.da.server.opc.job.JobHandler;
import org.openscada.da.server.opc.job.WorkUnit;
import org.openscada.da.server.opc.job.Worker;

public class JobApp implements JobHandler
{
    private static Logger logger = Logger.getLogger ( JobApp.class );

    public void test1 ()
    {
        Worker worker = new Worker ();
        for ( int i = 0; i < 10; i++ )
        {
            try
            {
                worker.execute ( new WorkUnit ( new SampleJob ( 100, 5000, false ), this ) );
            }
            catch ( final Throwable e )
            {
                logger.warn ( String.format ( "Failed in iteration #%d", i ), e );
                return;
            }
        }
        worker = null;
        System.gc ();
    }

    public void test2 ()
    {
        Worker worker = new Worker ();
        for ( int i = 0; i < 10; i++ )
        {
            try
            {
                worker.execute ( new WorkUnit ( new SampleJob ( 100, 5000, true ), this ) );
            }
            catch ( final Throwable e )
            {
                logger.warn ( String.format ( "Failed in iteration #%d", i ), e );
                return;
            }
        }
        worker = null;
        System.gc ();
    }

    public void test3 ()
    {
        final Random r = new Random ();

        Worker worker = new Worker ();
        for ( int i = 0; i < 1000; i++ )
        {
            try
            {
                final boolean peakJob = r.nextInt ( 100 ) > 90;
                long sleep = r.nextInt ( 100 );
                if ( peakJob )
                {
                    sleep *= 100;
                }
                worker.execute ( new WorkUnit ( new SampleJob ( r.nextInt ( 100 ), sleep, r.nextBoolean () ), this ) );
            }
            catch ( final Throwable e )
            {
                logger.warn ( String.format ( "Failed in iteration #%d", i ), e );
                return;
            }
        }
        worker = null;
        System.gc ();
    }

    public static void main ( final String[] args )
    {
        final JobApp app = new JobApp ();
        app.test1 ();
    }

    public void handleFailure ( final Throwable e )
    {
        logger.warn ( "Job failed", e );
    }

    public void handleInterrupted ()
    {
        logger.warn ( "Job got interrupted" );
    }

    public void handleSuccess ()
    {
        logger.warn ( "Job succeeded" );
    }
}
