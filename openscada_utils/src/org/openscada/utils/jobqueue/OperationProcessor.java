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

import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.openscada.utils.jobqueue.OperationManager.Handle;

public class OperationProcessor implements Runnable
{
    private static Logger _log = Logger.getLogger ( OperationProcessor.class );

    private Queue<Handle> _operationQueue = new LinkedList<Handle> ();

    private boolean _running = false;

    public void add ( Handle handle )
    {
        if ( handle == null )
            return;

        _log.debug ( String.format ( "Adding operation %d to processor", handle.getId () ) );

        synchronized ( _operationQueue )
        {
            _operationQueue.add ( handle );
            _operationQueue.notify ();
        }
    }

    public void run ()
    {
        synchronized ( this )
        {
            if ( _running )
                return;
            _running = true;
        }

        while ( _running )
        {
            try
            {
                process ();
            }
            catch ( InterruptedException e )
            {
                return;
            }
        }
    }

    private void process () throws InterruptedException
    {
        Handle handle = null;

        synchronized ( _operationQueue )
        {
            if ( _operationQueue.isEmpty () )
                _operationQueue.wait ();

            handle = _operationQueue.poll ();
        }
        if ( handle == null )
        {
            return;
        }
        _log.debug ( String.format ( "Running operation %d", handle.getId () ) );
        synchronized ( handle )
        {
            handle.start ();
            handle.wait ();
        }

    }

}
