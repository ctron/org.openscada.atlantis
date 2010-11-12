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

package org.openscada.da.server.simulation.component.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.openscada.core.Variant;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.simulation.component.Hive;

public class SimpleMOV extends BaseModule implements MOV, Runnable
{
    public enum State
    {
        CLOSED,
        OPENED,
        TRANSIT_OPEN,
        TRANSIT_CLOSE
    }

    protected final static int JOB_PERIOD = 1000;

    protected final static int MOV_RUNTIME = 10 * 1000;

    private final long _switchTime = MOV_RUNTIME;

    private long _lastTick = 0;

    private long _switchRunning = 0;

    private State _switchTarget = null;

    private boolean _error = false;

    private State _state = State.CLOSED;

    private DataItemInputChained _openInput = null;

    private DataItemInputChained _closeInput = null;

    private DataItemInputChained _transitInput = null;

    private DataItemInputChained _errorInput = null;

    private DataItemInputChained _runtimeInput = null;

    private DataItemInputChained _percentInput = null;

    private DataItemCommand _openCommand = null;

    private DataItemCommand _closeCommand = null;

    private ScheduledExecutorService executor = null;

    public SimpleMOV ( final Hive hive, final String id )
    {
        super ( hive, "mov." + id );

        this.executor = hive.getExecutor ();

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "tag", new Variant ( "mov." + id ) );
        this._openInput = getInput ( "open-signal", attributes );
        this._closeInput = getInput ( "close-signal", attributes );
        this._transitInput = getInput ( "transit-signal", attributes );
        this._errorInput = getInput ( "error-signal", attributes );
        this._runtimeInput = getInput ( "runtime-value", attributes );
        this._percentInput = getInput ( "percent-value", attributes );

        this._lastTick = System.currentTimeMillis ();
        this.executor.scheduleAtFixedRate ( this, 0, JOB_PERIOD, TimeUnit.MILLISECONDS );

        this._openCommand = getOutput ( "open-command", attributes );
        this._openCommand.addListener ( new DataItemCommand.Listener () {

            public void command ( final Variant value )
            {
                open ();
            }
        } );
        this._closeCommand = getOutput ( "close-command", attributes );
        this._closeCommand.addListener ( new DataItemCommand.Listener () {

            public void command ( final Variant value )
            {
                close ();
            }
        } );

        update ();
    }

    public synchronized void close ()
    {
        if ( this._state.equals ( State.CLOSED ) )
        {
            return;
        }

        startTransit ( State.CLOSED, State.TRANSIT_CLOSE );
    }

    public synchronized void open ()
    {
        if ( this._state.equals ( State.OPENED ) )
        {
            return;
        }

        startTransit ( State.OPENED, State.TRANSIT_OPEN );
    }

    public synchronized void startTransit ( final State target, final State currentState )
    {
        if ( this._switchTarget != null && this._switchTarget.equals ( target ) )
        {
            return;
        }

        if ( this._switchRunning > 0 )
        {
            this._switchRunning = this._switchTime - this._switchRunning;
        }
        else
        {
            this._switchRunning = this._switchTime;
        }

        this._switchTarget = target;
        this._state = currentState;

        update ();
    }

    public synchronized void setErrorState ( final boolean state )
    {
        if ( this._error = state )
        {
            return;
        }

        this._error = state;
    }

    protected void setOpenStates ( final boolean open, final boolean close )
    {
        this._openInput.updateData ( new Variant ( open ), null, null );
        this._closeInput.updateData ( new Variant ( close ), null, null );
        this._transitInput.updateData ( new Variant ( isTransit () ), null, null );
    }

    protected synchronized void update ()
    {
        if ( this._error )
        {
            setOpenStates ( true, true );
        }
        else
        {
            switch ( this._state )
            {
            case CLOSED:
                setOpenStates ( false, true );
                this._percentInput.updateData ( new Variant ( 0.0 ), null, null );
                break;
            case OPENED:
                setOpenStates ( true, false );
                this._percentInput.updateData ( new Variant ( 1.0 ), null, null );
                break;
            case TRANSIT_CLOSE:
                setOpenStates ( false, false );
                this._percentInput.updateData ( new Variant ( ( (double)this._switchRunning / (double)this._switchTime ) ), null, null );
                break;
            case TRANSIT_OPEN:
                setOpenStates ( false, false );
                this._percentInput.updateData ( new Variant ( 1 - (double)this._switchRunning / (double)this._switchTime ), null, null );
                break;
            }
        }
        this._errorInput.updateData ( new Variant ( this._error ), null, null );
        this._runtimeInput.updateData ( new Variant ( this._switchRunning ), null, null );

    }

    public boolean isTransit ()
    {
        switch ( this._state )
        {
        case TRANSIT_OPEN:
            return true;
        case TRANSIT_CLOSE:
            return true;
        default:
            return false;
        }
    }

    public synchronized void run ()
    {
        final long ts = System.currentTimeMillis ();
        final long diff = ts - this._lastTick;
        this._lastTick = ts;

        this._switchRunning -= diff;

        if ( this._switchRunning < 0 )
        {
            this._switchRunning = 0;
        }

        if ( this._switchTarget == null )
        {
            return;
        }

        if ( this._switchRunning == 0 )
        {
            this._state = this._switchTarget;
            this._switchTarget = null;
        }

        update ();
    }
}
