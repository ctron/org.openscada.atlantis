/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae.monitor.common;

import java.util.concurrent.Executor;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ds.DataListener;
import org.openscada.ds.DataNode;
import org.openscada.ds.DataStore;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Interner;

public abstract class AbstractPersistentStateMonitor extends AbstractStateMonitor implements DataItemMonitor
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractPersistentStateMonitor.class );

    private final SingleServiceListener<DataStore> listener = new SingleServiceListener<DataStore> () {

        @Override
        public void serviceChange ( final ServiceReference<DataStore> reference, final DataStore service )
        {
            AbstractPersistentStateMonitor.this.setDataStore ( service );
        }
    };

    private final SingleServiceTracker<DataStore> tracker;

    private DataStore dataStore;

    private final String nodeId;

    private final DataListener nodeListener = new DataListener () {

        @Override
        public void nodeChanged ( final DataNode node )
        {
            AbstractPersistentStateMonitor.this.nodeChanged ( node );
        }
    };

    private PersistentInformation lastStore;

    private final BundleContext context;

    public AbstractPersistentStateMonitor ( final String id, final String factoryId, final Executor executor, final BundleContext context, final Interner<String> stringInterner, final EventProcessor eventProcessor )
    {
        super ( id, executor, stringInterner, eventProcessor );

        this.context = context;

        this.tracker = new SingleServiceTracker<DataStore> ( context, DataStore.class, this.listener );

        this.nodeId = factoryId + "/" + id;

        this.tracker.open ();
    }

    protected synchronized void setDataStore ( final DataStore dataStore )
    {
        if ( this.dataStore != null )
        {
            this.dataStore.detachListener ( this.nodeId, this.nodeListener );
        }

        this.dataStore = dataStore;

        if ( this.dataStore != null )
        {
            this.dataStore.attachListener ( this.nodeId, this.nodeListener );
        }
    }

    @Override
    public void dispose ()
    {
        this.tracker.close ();
    }

    protected void nodeChanged ( final DataNode node )
    {
        logger.debug ( "Node changed: {}", node );

        if ( node == null )
        {
            return;
        }

        applyPersistentInformation ( (PersistentInformation)node.getDataAsObject ( this.context.getBundle (), null ) );

        // we can detach after the first update ... so actually we had an async-read
        this.dataStore.detachListener ( this.nodeId, this.nodeListener );

        // now, after we read what was we can overwrite with our current state
        if ( this.lastStore != null )
        {
            // write remembered store request
            logger.debug ( "Write remembered store request: {}", this.lastStore );
            this.dataStore.writeNode ( new DataNode ( this.nodeId, this.lastStore ) );
            this.lastStore = null;
        }
    }

    @Override
    protected synchronized void storePersistentInformation ( final PersistentInformation persistentInformation )
    {
        logger.debug ( "Request to store persistent information: {}", persistentInformation );

        if ( this.dataStore == null )
        {
            logger.debug ( "Remember store request for later" );
            this.lastStore = persistentInformation;
        }
        else
        {
            this.dataStore.writeNode ( new DataNode ( this.nodeId, persistentInformation ) );
        }
    }

}
