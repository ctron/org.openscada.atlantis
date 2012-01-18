/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.monitor.dataitem.monitor.internal.remote;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.Event.Fields;
import org.openscada.ae.MonitorStatus;
import org.openscada.ae.MonitorStatusInformation;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.MonitorListener;
import org.openscada.ae.monitor.MonitorService;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.master.AbstractMasterHandlerImpl;
import org.openscada.da.master.MasterItem;
import org.openscada.ds.DataListener;
import org.openscada.ds.DataNode;
import org.openscada.ds.DataStore;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericRemoteMonitor extends AbstractMasterHandlerImpl implements MonitorService
{
    private final static Logger logger = LoggerFactory.getLogger ( GenericRemoteMonitor.class );

    protected final String id;

    protected MonitorStatus state;

    protected Date timestamp;

    private final Set<MonitorListener> listeners = new HashSet<MonitorListener> ();

    protected final EventProcessor eventProcessor;

    protected final Executor executor;

    private String lastAckUser;

    private Date aknTimestamp;

    private final SingleServiceTracker<DataStore> tracker;

    private DataStore store;

    private final DataListener listener;

    private final BundleContext context;

    private PersistentState persistentState;

    public GenericRemoteMonitor ( final BundleContext context, final Executor executor, final ObjectPoolTracker poolTracker, final int priority, final String id, final EventProcessor eventProcessor )
    {
        super ( poolTracker, priority );
        this.context = context;
        this.executor = executor;
        this.eventProcessor = eventProcessor;
        this.id = id;

        this.state = MonitorStatus.INIT;

        this.listener = new DataListener () {

            @Override
            public void nodeChanged ( final DataNode node )
            {
                GenericRemoteMonitor.this.nodeChanged ( node );
            }
        };

        this.tracker = new SingleServiceTracker<DataStore> ( context, DataStore.class, new SingleServiceListener<DataStore> () {

            @Override
            public void serviceChange ( final ServiceReference<DataStore> reference, final DataStore service )
            {
                GenericRemoteMonitor.this.setStore ( service );
            }
        } );
        this.tracker.open ();
    }

    protected synchronized void nodeChanged ( final DataNode node )
    {
        Object o = null;

        if ( node != null )
        {
            o = node.getDataAsObject ( this.context.getBundle (), null );
        }

        if ( o == null )
        {
            o = new PersistentState ();
        }

        logger.info ( "Loaded persistent state: {} current state {}", new Object[] { o, this.state } );

        if ( ! ( o instanceof PersistentState ) )
        {
            return;
        }

        this.persistentState = (PersistentState)o;
        this.lastAckUser = this.persistentState.getLastAckUser ();

        final MonitorStatus currentState = this.state;
        this.state = this.persistentState.getState ();
        final Date currentTimestamp = this.timestamp;
        this.timestamp = this.persistentState.getTimestamp ();
        final Date currentAknTimestamp = this.aknTimestamp;
        this.aknTimestamp = this.persistentState.getAckTimestamp ();

        // now send the status
        final MonitorStatusInformation info = createStatus ();
        notifyListener ( info );

        if ( currentState != MonitorStatus.INIT )
        {
            // perform the transition to "now"
            setState ( currentState, currentTimestamp, currentAknTimestamp );
        }
    }

    protected synchronized void setStore ( final DataStore store )
    {
        if ( this.store != null )
        {
            this.store.detachListener ( getNodeId (), this.listener );
        }
        this.store = store;
        if ( this.store != null )
        {
            this.store.attachListener ( getNodeId (), this.listener );
        }
    }

    private String getNodeId ()
    {
        return getId () + "/remoteMonitor";
    }

    @Override
    public synchronized void dispose ()
    {
        this.tracker.close ();
        super.dispose ();
    }

    @Override
    public String getId ()
    {
        return this.id;
    }

    public void init ()
    {
    }

    protected void setState ( final MonitorStatus state )
    {
        setState ( state, new Date (), null );
    }

    protected synchronized void setState ( final MonitorStatus state, final Date timestamp, final Date aknTimestamp )
    {
        logger.debug ( "Update state - old: {}, new: {}", this.state, state );

        if ( this.state != state )
        {
            this.state = state;
            this.timestamp = timestamp;
            if ( aknTimestamp != null )
            {
                this.aknTimestamp = aknTimestamp;
            }
            logger.debug ( "State is: {}", state );

            doStore ();
            doNotify ();
        }
    }

    private void doStore ()
    {
        final DataStore store = this.store;
        if ( store != null )
        {
            final PersistentState state = new PersistentState ();
            state.setAckTimestamp ( this.aknTimestamp );
            state.setLastAckUser ( this.lastAckUser );
            state.setState ( this.state );
            state.setTimestamp ( this.timestamp );

            store.writeNode ( new DataNode ( getNodeId (), state ) );
        }
    }

    private void doNotify ()
    {
        final MonitorStatusInformation info = createStatus ();
        notifyListener ( info );

        if ( this.persistentState != null )
        {
            // only create events when we are initialized
            this.eventProcessor.publishEvent ( createEvent ( info, this.state.toString () ) );
        }
    }

    private synchronized void notifyListener ( final MonitorStatusInformation info )
    {
        final ArrayList<MonitorListener> listnersClone = new ArrayList<MonitorListener> ( this.listeners );
        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                for ( final MonitorListener listener : listnersClone )
                {
                    listener.statusChanged ( info );
                }
            }
        } );
    }

    protected static Calendar getTimestamp ( final DataItemValue itemValue, final String attributeName )
    {
        Calendar timestamp = null;
        if ( attributeName != null )
        {
            final Variant ts = itemValue.getAttributes ().get ( attributeName );
            if ( ts != null && ts.isNumber () )
            {
                timestamp = Calendar.getInstance ();
                timestamp.setTimeInMillis ( ts.asLong ( 0L ) );
            }
        }
        else
        {
            timestamp = itemValue.getTimestamp ();
        }

        if ( timestamp == null )
        {
            timestamp = Calendar.getInstance ();
        }
        return timestamp;
    }

    protected abstract DataItemValue handleUpdate ( final DataItemValue itemValue );

    @Override
    public synchronized DataItemValue dataUpdate ( final Map<String, Object> context, final DataItemValue value )
    {
        if ( value == null )
        {
            setState ( MonitorStatus.UNSAFE );
            return null;
        }

        return handleUpdate ( value );
    }

    private MonitorStatusInformation createStatus ()
    {
        Date timestamp = this.timestamp;
        if ( timestamp == null )
        {
            timestamp = new Date ();
        }

        if ( this.persistentState == null )
        {
            return new MonitorStatusInformation ( this.id, MonitorStatus.INIT, timestamp, null, this.aknTimestamp, this.lastAckUser, getMonitorAttributes () );
        }
        else
        {
            return new MonitorStatusInformation ( this.id, this.state, timestamp, null, this.aknTimestamp, this.lastAckUser, getMonitorAttributes () );
        }
    }

    protected Map<String, Variant> getMonitorAttributes ()
    {
        return this.eventAttributes;
    }

    /**
     * Create a pre-filled event builder
     * @return a new event builder
     */
    protected EventBuilder createEventBuilder ()
    {
        final EventBuilder builder = Event.create ();

        builder.sourceTimestamp ( new Date () );
        builder.entryTimestamp ( new Date () );

        builder.attributes ( this.eventAttributes );

        return builder;
    }

    private Event createEvent ( final MonitorStatusInformation info, final String eventType )
    {
        final EventBuilder builder = createEventBuilder ();

        builder.sourceTimestamp ( info.getStatusTimestamp () );
        builder.entryTimestamp ( new Date () );
        builder.attribute ( Fields.SOURCE, this.id );
        builder.attribute ( Fields.EVENT_TYPE, eventType );
        return builder.build ();
    }

    protected synchronized void publishAckRequestEvent ( final UserInformation user, final Date aknTimestamp )
    {
        final EventBuilder builder = createEventBuilder ();
        final Date now = new Date ();
        builder.sourceTimestamp ( now );
        builder.entryTimestamp ( now );
        builder.attribute ( Fields.SOURCE, this.id );
        builder.attribute ( Fields.EVENT_TYPE, "ACK-REQ" );
        if ( user != null && user.getName () != null )
        {
            builder.attribute ( Fields.ACTOR_NAME, user.getName () );
            this.lastAckUser = user.getName ();
        }
        else
        {
            this.lastAckUser = null;
        }

        // store the ack info
        doStore ();

        // fire change of last ack user
        notifyListener ( createStatus () );

        this.eventProcessor.publishEvent ( builder.build () );
    }

    protected Builder injectState ( final Builder builder )
    {
        builder.setAttribute ( this.id + ".state", Variant.valueOf ( this.state.toString () ) );

        boolean alarm = false;
        switch ( this.state )
        {
        case NOT_OK_AKN:
        case NOT_OK_NOT_AKN:
        case NOT_OK:
            alarm = true;
        }

        builder.setAttribute ( this.id + ".alarm", Variant.valueOf ( alarm ) );
        return builder;
    }

    @Override
    public synchronized void addStatusListener ( final MonitorListener listener )
    {
        if ( this.listeners.add ( listener ) )
        {
            final MonitorStatusInformation state = createStatus ();
            this.executor.execute ( new Runnable () {

                @Override
                public void run ()
                {
                    listener.statusChanged ( state );
                }
            } );
        }
    }

    @Override
    public synchronized void removeStatusListener ( final MonitorListener listener )
    {
        this.listeners.remove ( listener );
    }

    @Override
    protected void reprocess ()
    {
        if ( !getMasterItems ().isEmpty () )
        {
            this.executor.execute ( new Runnable () {

                @Override
                public void run ()
                {
                    logger.debug ( "Reprocessing {} master items", getMasterItems ().size () );

                    for ( final MasterItem item : getMasterItems () )
                    {
                        item.reprocess ();
                    }
                }
            } );
        }
    }

}
