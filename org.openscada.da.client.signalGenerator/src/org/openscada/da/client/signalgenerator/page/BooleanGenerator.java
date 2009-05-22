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

import org.eclipse.swt.widgets.Display;
import org.openscada.core.Variant;
import org.openscada.da.client.signalgenerator.SimulationTarget;

public class BooleanGenerator
{
    public enum State
    {
        START_DELAY,
        END_DELAY,
    }

    private Display display;

    private int startDelay = 1000;

    private int endDelay = 1000;

    private int iterations = -1;

    private boolean running = false;

    private long lastTick = 0;

    private State currentState;

    private final SimulationTarget target;

    public BooleanGenerator ( final Display display, final SimulationTarget target )
    {
        this.display = display;
        this.target = target;
    }

    public void start ()
    {
        if ( this.running )
        {
            return;
        }
        this.lastTick = System.currentTimeMillis ();
        this.currentState = State.START_DELAY;
        this.running = true;
        triggerRun ();
    }

    private void triggerRun ()
    {
        this.display.timerExec ( 100, new Runnable () {

            public void run ()
            {
                if ( BooleanGenerator.this.running )
                {
                    BooleanGenerator.this.tick ();
                    BooleanGenerator.this.triggerRun ();
                }
            }
        } );
    }

    public void stop ()
    {
        if ( !this.running )
        {
            this.running = false;
            return;
        }
    }

    public void dispose ()
    {
        stop ();
        this.display = null;
    }

    protected void tick ()
    {
        final long now = System.currentTimeMillis ();
        switch ( this.currentState )
        {
        case START_DELAY:
            if ( now - this.lastTick > this.startDelay )
            {
                this.currentState = State.END_DELAY;
                this.lastTick = now;
                this.target.writeValue ( new Variant ( true ) );
            }
            break;
        case END_DELAY:
            if ( now - this.lastTick > this.endDelay )
            {
                this.currentState = State.START_DELAY;
                this.lastTick = now;
                if ( this.iterations > 0 )
                {
                    this.iterations--;
                }
                this.target.writeValue ( new Variant ( false ) );
            }
            break;
        }

        if ( this.iterations == 0 )
        {
            this.running = false;
        }
    }

    public int getStartDelay ()
    {
        return this.startDelay;
    }

    public void setStartDelay ( final int startDelay )
    {
        this.startDelay = startDelay;
    }

    public int getEndDelay ()
    {
        return this.endDelay;
    }

    public void setEndDelay ( final int endDelay )
    {
        this.endDelay = endDelay;
    }

    public int getIterations ()
    {
        return this.iterations;
    }

    public void setIterations ( final int iterations )
    {
        this.iterations = iterations;
    }
}
