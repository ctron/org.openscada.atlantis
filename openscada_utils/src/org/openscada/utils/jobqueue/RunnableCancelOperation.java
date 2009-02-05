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

package org.openscada.utils.jobqueue;

import org.openscada.utils.jobqueue.OperationManager.Handle;

public abstract class RunnableCancelOperation implements Operation, Runnable
{
    private boolean _canceled = false;

    private Thread _thread = null;

    public synchronized void cancel () throws CancelNotSupportedException
    {
        _canceled = true;
    }

    public boolean isCanceled ()
    {
        return _canceled;
    }

    public void start ( final Handle handle )
    {
        final RunnableCancelOperation this_ = this;
        _thread = new Thread ( new Runnable () {

            public void run ()
            {
                try
                {
                    this_.run ();
                }
                finally
                {
                    handle.completed ();
                }
            }
        }, "RunnableCancelOperationThread" );

        _thread.start ();
    }

}
