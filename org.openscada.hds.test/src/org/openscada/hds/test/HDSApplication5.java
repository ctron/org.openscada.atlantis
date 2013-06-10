/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
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
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.hds.test;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openscada.hds.DataFileAccessor;
import org.openscada.hds.DataFileAccessorImpl;
import org.openscada.hds.DataFilePool;

public class HDSApplication5
{

    public static void main ( final String[] args ) throws Exception
    {
        final File base = new File ( "base", "data1" );

        FileUtils.deleteDirectory ( base.getParentFile () );
        base.mkdirs ();

        final DataFilePool pool = new DataFilePool ( 10000 );

        final File file1 = new File ( base, "f1" );

        {
            final DataFileAccessorImpl accessor = DataFileAccessorImpl.create ( file1, new Date (), new Date () );
            accessor.dispose ();
        }

        {
            final DataFileAccessor accessor1 = pool.getAccessor ( file1 );
            accessor1.dispose ();
            System.out.println ( "Try 1" );
        }

        {
            final DataFileAccessor accessor1 = pool.getAccessor ( file1 );
            accessor1.dispose ();
            System.out.println ( "Try 2" );
        }

        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor ();

        {
            final DataFileAccessor accessor1 = pool.getAccessor ( file1 );

            try
            {
                System.out.println ( "Try 3 - 1" );

                executorService.schedule ( new Runnable () {
                    @Override
                    public void run ()
                    {
                        System.out.println ( "Dispose 1" );
                        accessor1.dispose ();
                        System.out.println ( "Disposed 1" );
                    }
                }, 3000, TimeUnit.MILLISECONDS );

                final DataFileAccessor accessor2 = pool.getAccessor ( file1 );

                if ( accessor2 != null )
                {
                    System.out.println ( "Finally received" );
                    accessor2.dispose ();
                }
            }
            finally
            {
                accessor1.dispose ();
            }
            System.out.println ( "Try 3 - 2" );

            accessor1.dispose ();
        }

        {
            final DataFileAccessor accessor1 = pool.getAccessor ( file1 );
            accessor1.dispose ();
            System.out.println ( "Try 4" );
        }

        pool.dispose ();

        executorService.shutdown ();
    }
}
