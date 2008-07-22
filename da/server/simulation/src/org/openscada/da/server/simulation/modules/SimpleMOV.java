package org.openscada.da.server.simulation.modules;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.simulation.Hive;
import org.openscada.utils.timing.Scheduler;

public class SimpleMOV extends BaseModule implements MOV, Runnable
{
    public enum State
    {
        CLOSED, OPENED, TRANSIT_OPEN, TRANSIT_CLOSE
    }

    protected final static int JOB_PERIOD = 1000;

    protected final static int MOV_RUNTIME = 10 * 1000;

    private long _switchTime = MOV_RUNTIME;

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

    private Scheduler _scheduler = null;

    public SimpleMOV ( Hive hive, String id )
    {
        super ( hive, "mov." + id );

        _scheduler = hive.getScheduler ();

        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "tag", new Variant ( "mov." + id ) );
        _openInput = getInput ( "open-signal", attributes );
        _closeInput = getInput ( "close-signal", attributes  );
        _transitInput = getInput ( "transit-signal", attributes  );
        _errorInput = getInput ( "error-signal", attributes  );
        _runtimeInput = getInput ( "runtime-value", attributes  );
        _percentInput = getInput ( "percent-value", attributes  );

        _lastTick = System.currentTimeMillis ();
        _scheduler.addJob ( this, JOB_PERIOD );

        _openCommand = getOutput ( "open-command", attributes  );
        _openCommand.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                open ();
            }
        } );
        _closeCommand = getOutput ( "close-command", attributes  );
        _closeCommand.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                close ();
            }
        } );

        update ();
    }

    public synchronized void close ()
    {
        if ( _state.equals ( State.CLOSED ) )
            return;

        startTransit ( State.CLOSED, State.TRANSIT_CLOSE );
    }

    public synchronized void open ()
    {
        if ( _state.equals ( State.OPENED ) )
            return;

        startTransit ( State.OPENED, State.TRANSIT_OPEN );
    }

    public synchronized void startTransit ( State target, State currentState )
    {
        if ( ( _switchTarget != null ) && _switchTarget.equals ( target ) )
            return;

        if ( _switchRunning > 0 )
        {
            _switchRunning = _switchTime - _switchRunning;
        }
        else
        {
            _switchRunning = _switchTime;
        }

        _switchTarget = target;
        _state = currentState;

        update ();
    }

    public synchronized void setErrorState ( boolean state )
    {
        if ( _error = state )
            return;

        _error = state;
    }

    protected void setOpenStates ( boolean open, boolean close )
    {
        _openInput.updateData ( new Variant ( open ), null, null );
        _closeInput.updateData ( new Variant ( close ), null, null );
        _transitInput.updateData ( new Variant ( isTransit () ), null, null );
    }

    protected synchronized void update ()
    {
        if ( _error )
        {
            setOpenStates ( true, true );
        }
        else
        {
            switch ( _state )
            {
            case CLOSED:
                setOpenStates ( false, true );
                _percentInput.updateData ( new Variant ( 0.0 ), null, null );
                break;
            case OPENED:
                setOpenStates ( true, false );
                _percentInput.updateData ( new Variant ( 1.0 ), null, null );
                break;
            case TRANSIT_CLOSE:
                setOpenStates ( false, false );
                _percentInput.updateData ( new Variant ( ( (double)_switchRunning / (double)_switchTime ) ), null, null );
                break;
            case TRANSIT_OPEN:
                setOpenStates ( false, false );
                _percentInput.updateData ( new Variant ( 1 - ( (double)_switchRunning / (double)_switchTime ) ), null, null );
                break;
            }
        }
        _errorInput.updateData ( new Variant ( _error ), null, null );
        _runtimeInput.updateData ( new Variant ( _switchRunning ), null, null );

    }

    public boolean isTransit ()
    {
        switch ( _state )
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
        long ts = System.currentTimeMillis ();
        long diff = ts - _lastTick;
        _lastTick = ts;

        _switchRunning -= diff;

        if ( _switchRunning < 0 )
            _switchRunning = 0;

        if ( _switchTarget == null )
            return;

        if ( _switchRunning == 0 )
        {
            _state = _switchTarget;
            _switchTarget = null;
        }

        update ();
    }
}
