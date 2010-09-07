/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.hd.perf.test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class Application implements IApplication
{

    private final ExecutorService executor = Executors.newFixedThreadPool ( Runtime.getRuntime ().availableProcessors () );

    public Object start ( final IApplicationContext context ) throws Exception
    {

        final Collection<Future<?>> tasks = new LinkedList<Future<?>> ();
        tasks.add ( this.executor.submit ( new ConnectionRunner ( "hd:net://localhost:1402", "h.1" ) ) );
        tasks.add ( this.executor.submit ( new ConnectionRunner ( "da:net://localhost:1402", "h.1" ) ) );
        tasks.add ( this.executor.submit ( new ConnectionRunner ( "hd:net://localhost:1402", "h.1" ) ) );
        tasks.add ( this.executor.submit ( new ConnectionRunner ( "hd:net://localhost:1402", "h.1" ) ) );

        for ( final Future<?> task : tasks )
        {
            task.get ();
        }

        this.executor.shutdown ();

        dumpPerformanData ();

        return null;
    }

    public static final Object QUERY = new Object ();

    private void dumpPerformanData () throws InterruptedException, IOException
    {
        Thread.sleep ( 5 * 1000 );
        System.gc ();

        Writer out = new FileWriter ( "/tmp/psdata.dot" );
        Tracker.dumpCollect ( out, Tracker.THREAD );
        out.close ();

        out = new FileWriter ( "/tmp/psdata.query.dot" );
        Tracker.dumpCollect ( out, QUERY );
        out.close ();
    }

    public void stop ()
    {
    }

}
