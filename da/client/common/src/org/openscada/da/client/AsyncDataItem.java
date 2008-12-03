/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A data item which performs the notification asynchronously
 * @author Jens Reimann
 *
 */
public class AsyncDataItem extends DataItem
{

    private final ExecutorService executor;

    public AsyncDataItem ( final String itemId )
    {
        this ( itemId, null );
    }

    public AsyncDataItem ( final String itemId, final ItemManager connection )
    {
        this ( itemId, connection, Executors.newSingleThreadExecutor ( new ThreadFactory () {

            private final AtomicInteger i = new AtomicInteger ( 0 );

            @Override
            public Thread newThread ( final Runnable r )
            {
                final Thread t = new Thread ( r, "AsyncDataItem/" + itemId + "#" + this.i.getAndIncrement () );
                t.setDaemon ( true );
                return t;
            }
        } ) );
    }

    public AsyncDataItem ( final String itemId, final ItemManager connection, final ExecutorService executor )
    {
        super ( itemId );

        this.executor = executor;

        if ( connection != null )
        {
            register ( connection );
        }
    }

    @Override
    public void notifyObservers ( final Object arg )
    {
        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                AsyncDataItem.super.notifyObservers ( arg );
            }
        } );
    }

    @Override
    protected void finalize () throws Throwable
    {
        this.executor.shutdown ();
        super.finalize ();
    }

}
