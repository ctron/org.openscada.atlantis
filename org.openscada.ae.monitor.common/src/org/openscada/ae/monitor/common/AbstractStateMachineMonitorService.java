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

package org.openscada.ae.monitor.common;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.Event.Fields;
import org.openscada.ae.MonitorStatus;
import org.openscada.ae.MonitorStatusInformation;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.common.StateInformation.State;
import org.openscada.core.Variant;
import org.openscada.sec.UserInformation;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractStateMachineMonitorService extends AbstractPersistentMonitorService
{
    private final static boolean DEBUG = Boolean.getBoolean ( "debug" );

    private final static Logger logger = LoggerFactory.getLogger ( AbstractMonitorService.class );

    private final static StateInformation DEFAULT_STATE;

    static
    {
        DEFAULT_STATE = new StateInformation ();
        DEFAULT_STATE.setActive ( false );
        DEFAULT_STATE.setState ( State.UNSAFE );
        DEFAULT_STATE.setRequireAck ( true );
        DEFAULT_STATE.setTimestamp ( new Date () );
    }

    private final EventProcessor eventProcessor;

    private StateInformation initialInformation;

    private StateInformation information = new StateInformation ();

    private boolean initSent = false;

    private Map<String, Variant> eventInformationAttributes;

    public AbstractStateMachineMonitorService ( final BundleContext context, final Executor executor, final EventProcessor eventProcessor, final String id )
    {
        super ( id, executor, context );
        this.eventProcessor = eventProcessor;
        this.eventInformationAttributes = Collections.emptyMap ();

        sendDebugMessage ( "Initializing" );
    }

    protected void sendDebugMessage ( final String message )
    {
        if ( !DEBUG )
        {
            return;
        }

        final EventBuilder builder = createEventBuilder ();

        builder.attribute ( Fields.MESSAGE, message );
        builder.attribute ( Fields.EVENT_TYPE, "DEBUG" );

        this.eventProcessor.publishEvent ( builder.build () );
    }

    /**
     * Create an event builder pre-filled with the information we have
     * 
     * @return a new event builder
     */
    protected EventBuilder createEventBuilder ()
    {
        final EventBuilder builder = Event.create ();
        final Date now = new Date ();
        builder.sourceTimestamp ( now );
        builder.entryTimestamp ( now );

        injectEventAttributes ( builder );

        return builder;
    }

    /**
     * Restore from persistent state
     * @param state the persisted state
     */
    @Override
    protected synchronized void setPersistentState ( final StateInformation state )
    {
        logger.debug ( "Setting persistent state: {}", state );

        final boolean doInit = this.initialInformation == null;

        if ( state == null )
        {
            this.initialInformation = new StateInformation ();
        }
        else
        {
            this.initialInformation = state;
        }

        if ( doInit )
        {
            sendDebugMessage ( String.format ( "Initialize from store - current: %s, stored: %s", this.information, this.initialInformation ) );
            final StateInformation newInformation = DEFAULT_STATE.apply ( this.initialInformation ).apply ( this.information );

            this.information = newInformation;

            // re-apply the current state
            applyState ( this.information, MonitorDecoratorAdapter.getNullDecorator () );
        }
    }

    @Override
    public void init ()
    {
        super.init ();
    }

    protected void setEventInformationAttributes ( final Map<String, Variant> informationAttributes )
    {
        if ( informationAttributes == null )
        {
            this.eventInformationAttributes = Collections.emptyMap ();
        }
        else
        {
            this.eventInformationAttributes = new HashMap<String, Variant> ( informationAttributes );
        }
    }

    protected Map<String, Variant> getEventInformationAttributes ()
    {
        if ( this.eventInformationAttributes == null )
        {
            return Collections.emptyMap ();
        }
        return Collections.unmodifiableMap ( this.eventInformationAttributes );
    }

    @Override
    protected synchronized void switchToInit ()
    {
        logger.warn ( "Switched back to init: {}", getId () );

        // FIXME: implement
        this.initSent = false;
        this.initialInformation = null;
        applyState ( this.information, MonitorDecoratorAdapter.getNullDecorator () );
    }

    protected synchronized void setFailure ( final Variant value, final Date timestamp, final MonitorDecorator eventDecorator )
    {
        if ( this.information.getState () != null && this.information.getState () == State.FAILED )
        {
            // no change
            return;
        }

        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setState ( State.FAILED );
        newInformation.setValue ( value );
        newInformation.setTimestamp ( timestamp );
        newInformation.setLastFailTimestamp ( timestamp );

        applyAndSendStatus ( newInformation, eventDecorator );
    }

    protected synchronized void setFailure ( final Variant value, final Date timestamp )
    {
        setFailure ( value, timestamp, MonitorDecoratorAdapter.getNullDecorator () );
    }

    protected synchronized void setOk ( final Variant value, Date timestamp, final MonitorDecorator eventDecorator )
    {
        if ( this.information.getState () != null && this.information.getState () == State.OK )
        {
            // no change
            return;
        }

        if ( timestamp == null )
        {
            timestamp = new Date ();
        }

        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setState ( State.OK );
        newInformation.setValue ( value );
        newInformation.setTimestamp ( timestamp );

        applyAndSendStatus ( newInformation, eventDecorator );
    }

    protected synchronized void setOk ( final Variant value, final Date timestamp )
    {
        setOk ( value, timestamp, MonitorDecoratorAdapter.getNullDecorator () );
    }

    protected synchronized void setUnsafe ( final MonitorDecorator eventDecorator )
    {
        if ( this.information.getState () != null && this.information.getState () == State.UNSAFE )
        {
            // no change
            return;
        }

        final Date now = new Date ();

        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setState ( State.UNSAFE );
        newInformation.setValue ( null );
        newInformation.setTimestamp ( now );

        applyAndSendStatus ( newInformation, eventDecorator );
    }

    protected synchronized void setUnsafe ()
    {
        setUnsafe ( MonitorDecoratorAdapter.getNullDecorator () );
    }

    public synchronized void setActive ( final boolean state, final MonitorDecorator eventDecorator )
    {
        if ( this.information.getActive () != null && this.information.getActive () == state )
        {
            // no change
            return;
        }
        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setActive ( state );

        publishEvent ( eventDecorator.decorate ( createEvent ( new Date (), null, "CFG", Variant.valueOf ( state ) ).attribute ( Fields.MESSAGE, "Change active state" ) ) );
        applyAndSendStatus ( newInformation );
    }

    @Override
    public synchronized void setActive ( final boolean state )
    {
        setActive ( state, MonitorDecoratorAdapter.getNullDecorator () );
    }

    public synchronized void akn ( final UserInformation userInformation, final Date aknTimestamp, final MonitorDecorator eventDecorator )
    {
        if ( !ackPending ( this.information ) )
        {
            logger.debug ( "No ack is pending - {} / {}", this.information, aknTimestamp );
            return;
        }

        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setLastAckTimestamp ( aknTimestamp );
        newInformation.setLastAckUser ( getUserName ( userInformation ) );
        newInformation.setTimestamp ( aknTimestamp );

        publishEvent ( eventDecorator.decorate ( createEvent ( null, userInformation, "AKN", null ) ) );
        applyAndSendStatus ( newInformation );
    }

    @Override
    public synchronized void akn ( final UserInformation userInformation, final Date aknTimestamp )
    {
        akn ( userInformation, aknTimestamp, MonitorDecoratorAdapter.getNullDecorator () );
    }

    public synchronized void setRequireAkn ( final boolean state, final MonitorDecorator eventDecorator )
    {
        if ( this.information.getRequireAck () != null && this.information.getRequireAck () == state )
        {
            // no change
            return;
        }

        final StateInformation newInformation = new StateInformation ( this.information );
        newInformation.setRequireAck ( state );

        publishEvent ( eventDecorator.decorate ( createEvent ( null, null, "CFG", Variant.valueOf ( state ) ).attribute ( Fields.MESSAGE, "Change require acknowledge state" ) ) );
        applyAndSendStatus ( newInformation );
    }

    public synchronized void setRequireAkn ( final boolean state )
    {
        setRequireAkn ( state, MonitorDecoratorAdapter.getNullDecorator () );
    }

    private String getUserName ( final UserInformation userInformation )
    {
        if ( userInformation == null )
        {
            return null;
        }
        return userInformation.getName ();
    }

    private EventBuilder createEvent ( final Date timestamp, final UserInformation userInformation, final String eventType, final Variant value )
    {
        final EventBuilder builder = createEventBuilder ();

        builder.attributes ( this.eventInformationAttributes );

        final Date now = new Date ();
        builder.entryTimestamp ( now );
        if ( timestamp != null )
        {
            builder.sourceTimestamp ( timestamp );
        }
        else
        {
            builder.sourceTimestamp ( now );
        }

        if ( userInformation != null && userInformation.getName () != null )
        {
            builder.attribute ( Fields.ACTOR_NAME, userInformation.getName () );
            builder.attribute ( Fields.ACTOR_TYPE, "USER" );
        }
        if ( value != null )
        {
            builder.attribute ( Fields.VALUE, value );
        }

        builder.attribute ( Fields.EVENT_TYPE, eventType );

        return builder;
    }

    protected boolean isActivated ()
    {
        return this.initialInformation != null;
    }

    private synchronized void applyState ( final StateInformation information, final MonitorDecorator monitorDecorator )
    {
        final MonitorStatusInformation csi;

        logger.debug ( "Apply new state: {} for {}", new Object[] { information, getId () } );

        this.information = information;

        if ( isActivated () )
        {
            // if we are initialized we send out our current status
            this.initSent = false;
            final Map<String, Variant> attributes = new HashMap<String, Variant> ();
            buildMonitorAttributes ( attributes );
            monitorDecorator.decorateMonitorStatus ( attributes );
            csi = new MonitorStatusInformation ( getId (), generateStatus ( information ), information.getTimestamp (), information.getValue (), information.getLastAckTimestamp (), information.getLastAckUser (), attributes );
            storeData ( this.information );
        }
        else
        {
            logger.debug ( "Skipping apply notification since we are still un-initialized" );

            // otherwise send out our dummy status until we got initialized
            if ( !this.initSent )
            {
                csi = new MonitorStatusInformation ( getId (), MonitorStatus.INIT, new Date (), Variant.NULL, null, null, null );
                this.initSent = true;
            }
            else
            {
                csi = null;
            }
        }

        if ( csi != null )
        {
            notifyStateChange ( csi );
        }

    }

    /**
     * Add monitor attributes to attribute list of new monitor state
     * @param attributes the attributes that will be provided to the new monitor state
     */
    protected void buildMonitorAttributes ( final Map<String, Variant> attributes )
    {
        attributes.putAll ( this.eventInformationAttributes );
    }

    private synchronized void applyAndSendStatus ( final StateInformation newInformation, final MonitorDecorator monitorDecorator )
    {
        final MonitorStatusInformation oldConditionState = this.currentState;
        applyState ( newInformation, monitorDecorator );
        final MonitorStatusInformation newConditionState = this.currentState;
        sendStatusWhenChanged ( oldConditionState, newConditionState, monitorDecorator );
    }

    private synchronized void applyAndSendStatus ( final StateInformation newInformation )
    {
        applyAndSendStatus ( newInformation, MonitorDecoratorAdapter.getNullDecorator () );
    }

    private synchronized void sendStatusWhenChanged ( final MonitorStatusInformation oldConditionState, final MonitorStatusInformation newConditionState, final MonitorDecorator monitorDecorator )
    {
        final MonitorStatus oldState = oldConditionState.getStatus ();
        final MonitorStatus newState = newConditionState.getStatus ();

        /*
         * Suppress events if:
         * a) the state did not change
         * b) the state is still INIT
         * c) the state is INACTIVE, active changes are reported separately 
         */
        if ( oldConditionState != newConditionState && oldState != MonitorStatus.INIT && newState != MonitorStatus.INIT && newState != MonitorStatus.INACTIVE )
        {
            publishEvent ( monitorDecorator.decorate ( createEvent ( newConditionState.getStatusTimestamp (), null, newConditionState.getStatus ().toString (), newConditionState.getValue () ) ) );
        }
    }

    private static MonitorStatus generateStatus ( final StateInformation information )
    {
        if ( information.getActive () == null || information.getRequireAck () == null )
        {
            return MonitorStatus.INIT;
        }
        else if ( !information.getActive () )
        {
            return MonitorStatus.INACTIVE;
        }
        else if ( information.getValue () == null || information.getState () == State.UNSAFE )
        {
            return MonitorStatus.UNSAFE;
        }

        if ( !information.getRequireAck () )
        {
            return information.getState () == State.OK ? MonitorStatus.OK : MonitorStatus.NOT_OK;
        }
        else
        {
            final boolean isAckPending = ackPending ( information );
            if ( information.getState () == State.OK )
            {
                return isAckPending ? MonitorStatus.NOT_AKN : MonitorStatus.OK;
            }
            else
            {
                return isAckPending ? MonitorStatus.NOT_OK_NOT_AKN : MonitorStatus.NOT_OK_AKN;
            }
        }
    }

    private static boolean ackPending ( final StateInformation information )
    {
        if ( !information.getRequireAck () )
        {
            return false;
        }
        if ( information.getLastFailTimestamp () == null )
        {
            return false;
        }
        if ( information.getLastAckTimestamp () == null )
        {
            return true;
        }
        return information.getLastFailTimestamp ().after ( information.getLastAckTimestamp () );
    }

    protected synchronized void publishEvent ( final EventBuilder builder )
    {
        if ( isActivated () )
        {
            this.eventProcessor.publishEvent ( builder.build () );
        }
    }

    protected void injectEventAttributes ( final EventBuilder builder )
    {
        builder.attributes ( this.eventInformationAttributes );

        builder.attribute ( Fields.SOURCE, getId () );
    }

}
