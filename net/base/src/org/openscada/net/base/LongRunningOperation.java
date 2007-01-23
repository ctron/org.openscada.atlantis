/**
 * 
 */
package org.openscada.net.base;

import org.apache.log4j.Logger;
import org.openscada.net.base.data.Message;
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningState;

public class LongRunningOperation implements org.openscada.utils.exec.LongRunningOperation
{
    private static Logger _log = Logger.getLogger ( LongRunningOperation.class );

    private LongRunningController _controller = null;
    private LongRunningListener _listener = null;

    private long _id = 0;
    private boolean _stopped = false;
    private boolean _stopSent = false;

    private LongRunningState _longRunningState = LongRunningState.REQUESTED;
    private Throwable _error = null;
    private Message _reply = null;

    protected LongRunningOperation ( LongRunningController controller, LongRunningListener listener )
    {
        super ();
        _controller = controller;
        _listener = listener;
    }

    protected long getId ()
    {
        return _id;
    }

    private synchronized void stateChange ( LongRunningState state, Message message, Throwable error )
    {
        _log.debug ( "LongRunningState change: " + state.toString () );

        _longRunningState = state;
        _reply = message;
        _error = error;

        if ( _listener != null )
        {
            _listener.stateChanged ( state, error );
        }
    }

    synchronized protected void fail ( Throwable error )
    {
        if ( _stopped )
            return;

        stateChange ( LongRunningState.FAILURE, null, error );

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

        stateChange ( LongRunningState.RUNNING, null, null );
    }

    synchronized protected void result ( Message message )
    {
        _log.debug ( String.format ( "Result: %d", _id ) );

        stateChange ( LongRunningState.SUCCESS, message, null );

        notifyAll ();
    }

    synchronized protected void stop ()
    {
        switch ( _longRunningState )
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

        stateChange ( LongRunningState.FAILURE, null, null );

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
        return _longRunningState.equals ( LongRunningState.SUCCESS ) || _longRunningState.equals ( LongRunningState.FAILURE );
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
    public LongRunningState getState ()
    {
        return _longRunningState;
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