/**
 * 
 */
package org.openscada.net.base;

import org.apache.log4j.Logger;
import org.openscada.net.base.LongRunningController.Listener;
import org.openscada.net.base.LongRunningController.State;
import org.openscada.net.base.data.Message;

public class LongRunningOperation implements org.openscada.utils.exec.LongRunningOperation
{
    private static Logger _log = Logger.getLogger ( LongRunningOperation.class );

    private LongRunningController _controller = null;
    private Listener _listener = null;

    private long _id = 0;
    private boolean _stopped = false;
    private boolean _stopSent = false;

    private State _state = State.REQUESTED;
    private Throwable _error = null;
    private Message _reply = null;

    protected LongRunningOperation ( LongRunningController controller, Listener listener )
    {
        _controller = controller;
        _listener = listener;
    }

    protected long getId ()
    {
        return _id;
    }

    private synchronized void stateChange ( State state, Message message, Throwable error )
    {
        _log.debug ( "State change: " + state.toString () );

        _state = state;
        _reply = message;
        _error = error;

        if ( _listener != null )
        {
            _listener.stateChanged ( state, message, error );
        }
    }

    synchronized protected void fail ( Throwable error )
    {
        if ( _stopped )
            return;

        stateChange ( State.FAILURE, null, error );

        notifyAll ();
    }

    synchronized protected void granted ( long id )
    {
        _log.debug ( String.format ( "Granted: %d", id ) );
        _id = id;

        if ( _stopped )
        {
            sendStop ();
            return;
        }

        stateChange ( State.RUNNING, null, null );
    }

    synchronized protected void result ( Message message )
    {
        _log.debug ( String.format ( "Result: %d", _id ) );

        stateChange ( State.SUCCESS, message, null );

        notifyAll ();
    }

    synchronized protected void stop ()
    {
        switch ( _state )
        {
        case SUCCESS:
        case FAILURE:
            return;
        default:
            break;
        }

        if ( _stopped )
            return;

        _stopped = true;

        if ( _id != 0 )
            sendStop ();

        stateChange ( State.FAILURE, null, null );

        notifyAll ();
    }

    synchronized private void sendStop ()
    {
        if ( _stopSent )
            return;

        _stopSent = true;

        _controller.sendStopCommand ( this );
    }

    /* (non-Javadoc)
     * @see org.openscada.net.base.ILongRunningOperation#isComplete()
     */
    synchronized public boolean isComplete ()
    {
        return _state.equals ( State.SUCCESS ) || _state.equals ( State.FAILURE );
    }

    /* (non-Javadoc)
     * @see org.openscada.net.base.ILongRunningOperation#cancel()
     */
    public void cancel ()
    {
        _controller.stop ( this );
    }

    /* (non-Javadoc)
     * @see org.openscada.net.base.ILongRunningOperation#getError()
     */
    public Throwable getError ()
    {
        return _error;
    }

    /* (non-Javadoc)
     * @see org.openscada.net.base.ILongRunningOperation#getReply()
     */
    public Message getReply ()
    {
        return _reply;
    }

    /* (non-Javadoc)
     * @see org.openscada.net.base.ILongRunningOperation#getState()
     */
    public State getState ()
    {
        return _state;
    }

    /* (non-Javadoc)
     * @see org.openscada.net.base.ILongRunningOperation#waitForCompletion()
     */
    synchronized public void waitForCompletion () throws InterruptedException
    {
        if ( isComplete () )
            return;

        wait ();
    }

    /* (non-Javadoc)
     * @see org.openscada.net.base.ILongRunningOperation#waitForCompletion(int)
     */
    synchronized public void waitForCompletion ( int timeout ) throws InterruptedException
    {
        if ( isComplete () )
            return;

        wait ( timeout );
    }
}