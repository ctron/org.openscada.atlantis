/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.da.server.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.openscada.core.InvalidOperationException;
import org.openscada.core.Variant;
import org.openscada.core.server.common.session.UserSession;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public class DataItemCommand extends DataItemOutput
{

    private static Logger logger = Logger.getLogger ( DataItemCommand.class );

    /**
     * The listener interface
     * @author Jens Reimann
     *
     */
    public static interface Listener
    {
        public void command ( Variant value ) throws Exception;
    }

    private final List<Listener> listeners = new CopyOnWriteArrayList<Listener> ();

    private final Executor executor;

    public DataItemCommand ( final String name, final Executor executor )
    {
        super ( name );
        this.executor = executor;
    }

    public NotifyFuture<WriteResult> startWriteValue ( final UserSession session, final Variant value )
    {
        final FutureTask<WriteResult> task = new FutureTask<WriteResult> ( new Callable<WriteResult> () {

            public WriteResult call () throws Exception
            {
                processWrite ( value );
                return null;
            }
        } );

        this.executor.execute ( task );

        return task;
    }

    public void processWrite ( final Variant value ) throws InvalidOperationException
    {
        for ( final Listener listener : this.listeners )
        {
            try
            {
                listener.command ( value );
            }
            catch ( final Throwable e )
            {
                logger.warn ( "Failed to run listener", e );
                throw new InvalidOperationException ();
            }
        }
    }

    /**
     * Add a new listener which gets called on write requests
     * @param listener listener to add
     */
    public void addListener ( final Listener listener )
    {
        this.listeners.add ( listener );
    }

    /**
     * Remove a listener from the list
     * @param listener listener to remove
     */
    public void removeListener ( final Listener listener )
    {
        this.listeners.remove ( listener );
    }

    public Map<String, Variant> getAttributes ()
    {
        return new HashMap<String, Variant> ();
    }

    public NotifyFuture<WriteAttributeResults> startSetAttributes ( final UserSession session, final Map<String, Variant> attributes )
    {
        return new InstantFuture<WriteAttributeResults> ( WriteAttributesHelper.errorUnhandled ( null, attributes ) );
    }

}
