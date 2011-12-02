/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.server.common.event.pool.internal;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.ae.event.EventManager;
import org.openscada.ae.server.common.event.EventQuery;
import org.openscada.ae.server.storage.Storage;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.openscada.utils.osgi.FilterUtil;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventPoolManager
{

    private final static Logger logger = LoggerFactory.getLogger ( EventPoolManager.class );

    private final BundleContext context;

    private final SingleServiceTracker<?> eventManagerTracker;

    private final SingleServiceTracker<?> storageTracker;

    private Storage storage;

    private EventManager eventManager;

    private EventPoolImpl pool;

    private ServiceRegistration<?> poolHandle;

    private final String id;

    private final ExecutorService executor;

    private String filter;

    private int size;

    public EventPoolManager ( final BundleContext context, final String id, final String filter, final int size ) throws InvalidSyntaxException
    {
        this.context = context;
        this.id = id;
        this.filter = filter;
        this.size = size;

        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( "EventPoolManager/" + id ) );

        this.eventManagerTracker = new SingleServiceTracker<Object> ( this.context, FilterUtil.createClassFilter ( EventManager.class.getName () ), new SingleServiceListener<Object> () {

            @Override
            public void serviceChange ( final ServiceReference<Object> reference, final Object service )
            {
                EventPoolManager.this.setEventManager ( (EventManager)service );
            }
        } );
        this.eventManagerTracker.open ();

        this.storageTracker = new SingleServiceTracker<Object> ( this.context, FilterUtil.createClassFilter ( Storage.class.getName () ), new SingleServiceListener<Object> () {

            @Override
            public void serviceChange ( final ServiceReference<Object> reference, final Object service )
            {
                EventPoolManager.this.setStorageService ( (Storage)service );
            }
        } );
        this.storageTracker.open ();
    }

    protected synchronized void setStorageService ( final Storage service )
    {
        this.storage = service;
        checkInit ();
    }

    protected synchronized void setEventManager ( final EventManager service )
    {
        this.eventManager = service;
        checkInit ();
    }

    private void checkInit ()
    {
        // FIXME: async exec

        if ( this.storage != null && this.eventManager != null )
        {
            disposePool ();
            createPool ( this.storage, this.eventManager );
        }
        else
        {
            disposePool ();
        }
    }

    private void createPool ( final Storage storage, final EventManager eventManager )
    {
        logger.info ( "Create pool: {}", this.id );
        try
        {
            this.pool = new EventPoolImpl ( this.executor, storage, eventManager, this.filter, this.size );

            this.pool.start ();

            logger.info ( "pool {} created", this.id );

            final Dictionary<String, String> properties = new Hashtable<String, String> ();
            properties.put ( Constants.SERVICE_PID, this.id );
            this.poolHandle = this.context.registerService ( EventQuery.class.getName (), this.pool, properties );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to create event pool: " + this.id, e );
        }
    }

    public void dispose ()
    {
        disposePool ();
        this.executor.shutdown ();
    }

    private synchronized void disposePool ()
    {
        logger.info ( "Dispose pool: {}", this.id );

        if ( this.poolHandle != null )
        {
            logger.debug ( "Unregister pool" );
            this.poolHandle.unregister ();
            this.poolHandle = null;
        }
        if ( this.pool != null )
        {
            this.pool.stop ();
            this.pool = null;
        }
    }

    public void update ( final String filter, final int size )
    {
        this.filter = filter;
        this.size = size;
        checkInit ();
    }
}
