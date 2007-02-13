/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
            _listener.stateChanged ( this, state, error );
        }
    }

    protected synchronized void fail ( Throwable error )
    {
        stateChange ( LongRunningState.FAILURE, null, error );

        notifyAll ();
    }

    protected synchronized void granted ( long id )
    {
        _log.debug ( String.format ( "Granted: %d", id ) );
        _id = id;

        stateChange ( LongRunningState.RUNNING, null, null );
    }

    protected synchronized void result ( Message message )
    {
        _log.debug ( String.format ( "Result: %d", _id ) );

        stateChange ( LongRunningState.SUCCESS, message, null );

        notifyAll ();
    }

    /* (non-Javadoc)
     * @see org.openscada.net.base.ILongRunningOperation#isComplete()
     */
    public synchronized boolean isComplete ()
    {
        return _longRunningState.equals ( LongRunningState.SUCCESS )
                || _longRunningState.equals ( LongRunningState.FAILURE );
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
    public synchronized void waitForCompletion () throws InterruptedException
    {
        if ( isComplete () )
        {
            return;
        }

        wait ();
    }

    /* (non-Javadoc)
     * @see org.openscada.net.base.ILongRunningOperation#waitForCompletion(int)
     */
    public synchronized void waitForCompletion ( int timeout ) throws InterruptedException
    {
        if ( isComplete () )
        {
            return;
        }

        wait ( timeout );
    }
}