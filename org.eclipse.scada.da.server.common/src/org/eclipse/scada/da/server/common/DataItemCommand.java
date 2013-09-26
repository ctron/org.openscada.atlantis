/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.eclipse.scada.da.server.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

import org.eclipse.scada.core.InvalidOperationException;
import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.server.OperationParameters;
import org.eclipse.scada.da.core.WriteAttributeResults;
import org.eclipse.scada.da.core.WriteResult;
import org.eclipse.scada.utils.concurrent.FutureTask;
import org.eclipse.scada.utils.concurrent.InstantFuture;
import org.eclipse.scada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataItemCommand extends DataItemOutput
{

    private final static Logger logger = LoggerFactory.getLogger ( DataItemCommand.class );

    /**
     * The listener interface
     * 
     * @author Jens Reimann
     */
    public static interface Listener
    {
        public void command ( Variant value ) throws Exception;
    }

    private final List<Listener> listeners = new CopyOnWriteArrayList<Listener> ();

    private final Executor executor;

    public DataItemCommand ( final String id, final Executor executor )
    {
        super ( id );
        this.executor = executor;
    }

    @Override
    public NotifyFuture<WriteResult> startWriteValue ( final Variant value, final OperationParameters operationParameters )
    {
        final FutureTask<WriteResult> task = new FutureTask<WriteResult> ( new Callable<WriteResult> () {

            @Override
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
     * 
     * @param listener
     *            listener to add
     */
    public void addListener ( final Listener listener )
    {
        this.listeners.add ( listener );
    }

    /**
     * Remove a listener from the list
     * 
     * @param listener
     *            listener to remove
     */
    public void removeListener ( final Listener listener )
    {
        this.listeners.remove ( listener );
    }

    @Override
    public Map<String, Variant> getAttributes ()
    {
        return new HashMap<String, Variant> ();
    }

    @Override
    public NotifyFuture<WriteAttributeResults> startSetAttributes ( final Map<String, Variant> attributes, final OperationParameters operationParameters )
    {
        return new InstantFuture<WriteAttributeResults> ( WriteAttributesHelper.errorUnhandled ( null, attributes ) );
    }

}
