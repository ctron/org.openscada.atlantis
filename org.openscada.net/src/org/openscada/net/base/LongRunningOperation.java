/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.net.base;

import org.apache.log4j.Logger;
import org.openscada.net.base.data.Message;
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningState;

public class LongRunningOperation implements org.openscada.utils.exec.LongRunningOperation
{
    private static Logger log = Logger.getLogger ( LongRunningOperation.class );

    @SuppressWarnings ( "unused" )
    private LongRunningController controller = null;

    private LongRunningListener listener = null;

    private long id = 0;

    private LongRunningState longRunningState = LongRunningState.REQUESTED;

    private Throwable error = null;

    private Message reply = null;

    protected LongRunningOperation ( final LongRunningController controller, final LongRunningListener listener )
    {
        super ();
        this.controller = controller;
        this.listener = listener;
    }

    protected long getId ()
    {
        return this.id;
    }

    private synchronized void stateChange ( final LongRunningState state, final Message message, final Throwable error )
    {
        log.debug ( "LongRunningState change: " + state.toString () );

        this.longRunningState = state;
        this.reply = message;
        this.error = error;

        if ( this.listener != null )
        {
            this.listener.stateChanged ( this, state, error );
        }
    }

    protected synchronized void fail ( final Throwable error )
    {
        stateChange ( LongRunningState.FAILURE, null, error );

        notifyAll ();
    }

    protected synchronized void granted ( final long id )
    {
        log.debug ( String.format ( "Granted: %d", id ) );
        this.id = id;

        stateChange ( LongRunningState.RUNNING, null, null );
    }

    protected synchronized void result ( final Message message )
    {
        log.debug ( String.format ( "Result: %d", this.id ) );

        stateChange ( LongRunningState.SUCCESS, message, null );

        notifyAll ();
    }

    /* (non-Javadoc)
     * @see org.openscada.net.base.ILongRunningOperation#isComplete()
     */
    public synchronized boolean isComplete ()
    {
        return this.longRunningState.equals ( LongRunningState.SUCCESS ) || this.longRunningState.equals ( LongRunningState.FAILURE );
    }

    /* (non-Javadoc)
     * @see org.openscada.net.base.ILongRunningOperation#getError()
     */
    public Throwable getError ()
    {
        return this.error;
    }

    /* (non-Javadoc)
     * @see org.openscada.net.base.ILongRunningOperation#getReply()
     */
    public Message getReply ()
    {
        return this.reply;
    }

    /* (non-Javadoc)
     * @see org.openscada.net.base.ILongRunningOperation#getState()
     */
    public LongRunningState getState ()
    {
        return this.longRunningState;
    }

    /* (non-Javadoc)
     * @see org.openscada.net.base.ILongRunningOperation#waitForCompletion()
     */
    public synchronized void waitForCompletion () throws InterruptedException
    {
        while ( !isComplete () )
        {
            wait ();
        }
    }

    /* (non-Javadoc)
     * @see org.openscada.net.base.ILongRunningOperation#waitForCompletion(int)
     */
    public synchronized void waitForCompletion ( final int timeout ) throws InterruptedException
    {
        while ( !isComplete () )
        {
            wait ( timeout );
        }
    }
}