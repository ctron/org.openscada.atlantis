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

package org.openscada.ae.monitor.common;

import java.util.concurrent.Executor;

import org.openscada.ds.DataListener;
import org.openscada.ds.DataNode;
import org.openscada.ds.DataStore;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public abstract class AbstractPersistentMonitorService extends AbstractMonitorService
{

    private final BundleContext context;

    private final ServiceTracker<DataStore, DataStore> tracker;

    private final DataListener storeListener;

    private DataStore store;

    private boolean disposed = false;

    public AbstractPersistentMonitorService ( final String id, final Executor executor, final BundleContext context )
    {
        super ( id, executor );
        this.context = context;

        this.storeListener = new DataListener () {

            @Override
            public void nodeChanged ( final DataNode node )
            {
                AbstractPersistentMonitorService.this.nodeChanged ( node );
            }
        };

        // setup hooks
        this.tracker = new ServiceTracker<DataStore, DataStore> ( this.context, DataStore.class, new ServiceTrackerCustomizer<DataStore, DataStore> () {

            @Override
            public void removedService ( final ServiceReference<DataStore> reference, final DataStore service )
            {
                AbstractPersistentMonitorService.this.context.ungetService ( reference );
                setDataStore ( null );
            }

            @Override
            public void modifiedService ( final ServiceReference<DataStore> reference, final DataStore service )
            {
            }

            @Override
            public DataStore addingService ( final ServiceReference<DataStore> reference )
            {
                final DataStore store = AbstractPersistentMonitorService.this.context.getService ( reference );
                setDataStore ( store );
                return store;
            }
        } );

    }

    public void init ()
    {
        this.tracker.open ();
    }

    protected String getNodeId ()
    {
        return getId () + "/monitorState"; //$NON-NLS-1$
    }

    public synchronized void dispose ()
    {
        this.disposed = true;

        if ( this.store != null )
        {
            this.store.detachListener ( getNodeId (), this.storeListener );
            this.store = null;
        }

        this.tracker.close ();
    }

    protected synchronized void setDataStore ( final DataStore store )
    {
        if ( this.store != null )
        {
            this.store.detachListener ( getNodeId (), this.storeListener );
        }

        this.store = store;

        if ( this.store != null )
        {
            this.store.attachListener ( getNodeId (), this.storeListener );
        }
        else
        {
            if ( !this.disposed )
            {
                switchToInit ();
            }
        }
    }

    protected synchronized void storeData ( final StateInformation state )
    {
        if ( this.store != null )
        {
            this.store.writeNode ( new DataNode ( getNodeId (), state ) );
        }
    }

    protected void nodeChanged ( final DataNode node )
    {
        if ( this.disposed )
        {
            return;
        }

        if ( node == null )
        {
            setPersistentState ( null );
        }
        else
        {
            final Object o = node.getDataAsObject ( this.context.getBundle (), null );
            if ( o instanceof StateInformation )
            {
                setPersistentState ( (StateInformation)o );
            }
            else
            {
                setPersistentState ( null );
            }
        }
    }

    /**
     * Switch back to the init state (standby)
     */
    protected abstract void switchToInit ();

    protected abstract void setPersistentState ( StateInformation state );

}
