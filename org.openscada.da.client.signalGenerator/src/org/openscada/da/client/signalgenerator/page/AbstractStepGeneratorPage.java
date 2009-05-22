/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.signalgenerator.page;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.openscada.da.client.signalgenerator.Activator;
import org.openscada.da.client.signalgenerator.SimulationTarget;

public abstract class AbstractStepGeneratorPage implements GeneratorPage
{

    private long lastTimestamp;

    protected Display display;

    private volatile boolean running;

    private final long period = 250;

    private final int tickDelay = 100;

    protected SimulationTarget target;

    protected abstract void step ();

    public void dispose ()
    {
        stop ();
    }

    public void setDisplay ( final Display display )
    {
        this.display = display;
    }

    public void start ()
    {
        this.lastTimestamp = 0;
        this.running = true;
        setTimer ();
    }

    private void setTimer ()
    {
        this.display.timerExec ( this.tickDelay, new Runnable () {
            public void run ()
            {
                AbstractStepGeneratorPage.this.tick ();
            }
        } );
    }

    public void stop ()
    {
        this.running = false;
    }

    private void tick ()
    {
        if ( !this.running )
        {
            return;
        }

        final long now = System.currentTimeMillis ();
        try
        {
            if ( now - this.lastTimestamp < this.period )
            {
                step ();
            }
        }
        catch ( final Throwable e )
        {
            Activator.getDefault ().getLog ().log ( new Status ( Status.ERROR, Activator.PLUGIN_ID, "Failed to step", e ) ); //$NON-NLS-1$
            e.printStackTrace ();
        }
        finally
        {
            this.lastTimestamp = now;
            setTimer ();
        }

    }

    public void setTarget ( final SimulationTarget target )
    {
        this.target = target;
    }

}