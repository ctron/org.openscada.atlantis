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

package org.openscada.utils.exec;

import java.util.concurrent.TimeUnit;

/**
 * A class that can be used to track the progress of an asynchronous execution
 * 
 * @author jens
 * @param <R>
 */
public class OperationResult<R>
{
    private OperationResultHandler<R> _handler = null;

    private R _result = null;

    private Exception _exception = null;

    private boolean _complete = false;

    public OperationResult ()
    {
        this ( null );
    }

    public OperationResult ( OperationResultHandler<R> handler )
    {
        _handler = handler;
    }

    public synchronized boolean isComplete ()
    {
        return _complete;
    }

    public synchronized boolean isSuccess ()
    {
        return _exception == null;
    }

    public synchronized Exception getException ()
    {
        return _exception;
    }

    public synchronized R getResult ()
    {
        return _result;
    }

    public synchronized R get ()
    {
        return getResult ();
    }

    public synchronized void notifySuccess ( R result )
    {
        if ( _complete )
            return;

        _complete = true;
        _result = result;
        _exception = null;

        notifyAll ();

        if ( _handler != null )
            _handler.success ( result );
    }

    public synchronized void notifyFailure ( Exception e )
    {
        if ( _complete )
            return;

        _complete = true;
        _result = null;
        _exception = e;

        notifyAll ();

        if ( _handler != null )
            _handler.failure ( e );
    }

    public synchronized void complete () throws InterruptedException
    {
        if ( _complete )
            return;

        wait ();
    }

    public synchronized boolean complete ( long timeout, TimeUnit t ) throws InterruptedException
    {
        if ( _complete )
            return true;

        wait ( t.toMillis ( timeout ) );

        return _complete;
    }
}
