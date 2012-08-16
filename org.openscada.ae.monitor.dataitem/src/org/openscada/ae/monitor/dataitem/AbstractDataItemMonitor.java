/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.monitor.dataitem;

import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.MonitorStatus;
import org.openscada.ae.MonitorStatusInformation;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.common.AbstractStateMachineMonitorService;
import org.openscada.ae.monitor.common.Severity;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.master.MasterItem;
import org.openscada.da.master.MasterItemHandler;
import org.openscada.da.master.WriteRequest;
import org.openscada.da.master.WriteRequestResult;
import org.openscada.sec.UserInformation;
import org.openscada.utils.interner.InternerHelper;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.openscada.utils.osgi.pool.SingleObjectPoolServiceTracker;
import org.openscada.utils.osgi.pool.SingleObjectPoolServiceTracker.ServiceListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Interner;

public abstract class AbstractDataItemMonitor extends AbstractStateMachineMonitorService implements DataItemMonitor
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractDataItemMonitor.class );

    private String masterId;

    private SingleObjectPoolServiceTracker<MasterItem> tracker;

    protected MasterItem masterItem;

    private MasterItemHandler handler;

    protected final String prefix;

    private boolean requireAkn = false;

    private boolean active = true;

    private boolean akn;

    private MonitorStatus state;

    private int handlerPriority;

    private boolean alarm;

    private boolean unsafe;

    private final String defaultMonitorType;

    private String monitorType;

    private final ObjectPoolTracker<MasterItem> poolTracker;

    private Boolean initialUpdate;

    private final Executor executor;

    protected Interner<String> stringInterner;

    private Severity severity;

    public AbstractDataItemMonitor ( final BundleContext context, final Executor executor, final Interner<String> stringInterner, final ObjectPoolTracker<MasterItem> poolTracker, final EventProcessor eventProcessor, final String id, final String prefix, final String defaultMonitorType )
    {
        super ( context, executor, eventProcessor, id );
        this.executor = executor;
        this.poolTracker = poolTracker;
        this.prefix = prefix;
        this.defaultMonitorType = defaultMonitorType;
        this.stringInterner = stringInterner == null ? InternerHelper.makeNoOpInterner () : stringInterner;
    }

    @Override
    public synchronized void dispose ()
    {
        disconnect ();
    }

    protected Map<String, Variant> convertAttributes ( final ConfigurationDataHelper cfg )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        for ( final Map.Entry<String, String> entry : cfg.getPrefixed ( "info." ).entrySet () )
        {
            attributes.put ( intern ( entry.getKey () ), Variant.valueOf ( entry.getValue () ) );
        }

        return attributes;
    }

    @Override
    public synchronized void update ( final UserInformation userInformation, final Map<String, String> properties ) throws Exception
    {
        disconnect ();

        if ( this.initialUpdate != null && this.initialUpdate == true )
        {
            this.initialUpdate = false;
        }
        if ( this.initialUpdate == null )
        {
            this.initialUpdate = true;
        }

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( properties );

        this.masterId = intern ( cfg.getStringChecked ( MasterItem.MASTER_ID, String.format ( "'%s' must be set", MasterItem.MASTER_ID ) ) );
        this.handlerPriority = cfg.getInteger ( "handlerPriority", getDefaultHandlerPriority () );
        this.monitorType = intern ( cfg.getString ( "monitorType", this.defaultMonitorType ) );

        setEventInformationAttributes ( userInformation, convertAttributes ( cfg ) );
        setActive ( userInformation, cfg.getBoolean ( "active", true ) );
        setRequireAkn ( userInformation, cfg.getBoolean ( "requireAck", false ) );
        setSeverity ( userInformation, makeSeverity ( cfg.getString ( "severity" ) ) );

        connect ();
    }

    /**
     * Create the severity for this monitor based on the configuration string
     * 
     * @param string
     *            the configuration string
     * @return a {@link Severity}
     */
    protected Severity makeSeverity ( final String string )
    {
        try
        {
            return Severity.valueOf ( string );
        }
        catch ( final Exception e )
        {
            return Severity.ERROR;
        }
    }

    protected boolean isInitialUpdate ()
    {
        final Boolean initial = this.initialUpdate;
        if ( initial == null )
        {
            return true;
        }
        else
        {
            return initial;
        }
    }

    protected int getDefaultHandlerPriority ()
    {
        return 0;
    }

    private synchronized void connect () throws InvalidSyntaxException
    {
        if ( this.masterId == null )
        {
            setUnsafe ();
            throw new RuntimeException ( String.format ( "'%s' is not set", MasterItem.MASTER_ID ) );
        }

        logger.debug ( "Setting up for master item: {}", this.masterId );

        this.tracker = new SingleObjectPoolServiceTracker<MasterItem> ( this.poolTracker, this.masterId, new ServiceListener<MasterItem> () {

            @Override
            public void serviceChange ( final MasterItem service, final Dictionary<?, ?> properties )
            {
                AbstractDataItemMonitor.this.setMasterItem ( service );
            }
        } );

        this.tracker.open ();
    }

    protected void setMasterItem ( final MasterItem masterItem )
    {
        logger.info ( "Setting master item: {}", masterItem );

        this.executor.execute ( new Runnable () {
            @Override
            public void run ()
            {
                performSet ( masterItem );
            }
        } );
    }

    protected synchronized void performSet ( final MasterItem masterItem )
    {
        logger.info ( "Perform set item: {}", masterItem );

        disconnectItem ();
        connectItem ( masterItem );
    }

    private synchronized void connectItem ( final MasterItem masterItem )
    {
        logger.debug ( "Connecting to master item: {}", masterItem );

        this.masterItem = masterItem;
        if ( this.masterItem != null )
        {
            this.masterItem.addHandler ( this.handler = new MasterItemHandler () {

                @Override
                public WriteRequestResult processWrite ( final WriteRequest request )
                {
                    return AbstractDataItemMonitor.this.handleProcessWrite ( request );
                }

                @Override
                public DataItemValue dataUpdate ( final Map<String, Object> context, final DataItemValue value )
                {
                    logger.debug ( "Handle data update: {}", value );
                    return AbstractDataItemMonitor.this.handleDataUpdate ( value );
                }
            }, this.handlerPriority );
        }
    }

    private synchronized void disconnectItem ()
    {
        logger.debug ( "Disconnect from master item: {}", this.masterItem );

        if ( this.masterItem != null )
        {
            this.masterItem.removeHandler ( this.handler );
            this.masterItem = null;
            this.handler = null;
        }
    }

    private synchronized void disconnect ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
            this.tracker = null;
        }
    }

    private DataItemValue handleDataUpdate ( final DataItemValue value )
    {
        final DataItemValue.Builder builder = new DataItemValue.Builder ( value );

        performDataUpdate ( builder );
        injectAttributes ( builder );

        final DataItemValue newValue = builder.build ();
        logger.debug ( "Setting new value: {}", newValue );

        return newValue;
    }

    protected abstract void performDataUpdate ( Builder builder );

    /**
     * Return the factory id that configured this instance
     * 
     * @return the factory id
     */
    protected abstract String getFactoryId ();

    /**
     * Return the configuration id that is assigned to this instance
     * 
     * @return the configuration id
     */
    protected abstract String getConfigurationId ();

    @Override
    protected synchronized void notifyStateChange ( final MonitorStatusInformation status )
    {
        super.notifyStateChange ( status );
        this.state = status.getStatus ();
        this.akn = this.state == MonitorStatus.NOT_AKN || this.state == MonitorStatus.NOT_OK_NOT_AKN;
        this.unsafe = this.state == MonitorStatus.UNSAFE;
        this.alarm = this.state == MonitorStatus.NOT_OK || this.state == MonitorStatus.NOT_OK_AKN || this.state == MonitorStatus.NOT_OK_NOT_AKN;
        reprocess ();
    }

    /**
     * Get the current active severity.
     * <p>
     * This should be the configured severity, but may be overriden.
     * </p>
     * 
     * @return the current active severity
     */
    protected Severity getSeverity ()
    {
        return this.severity;
    }

    protected boolean isActive ()
    {
        return this.active;
    }

    /**
     * Inject attributes to the value after the value update has been performed using {@link #performDataUpdate(Builder)}
     * 
     * @param builder
     *            the builder to use for changing information
     */
    protected void injectAttributes ( final Builder builder )
    {
        builder.setAttribute ( intern ( this.prefix + ".active" ), Variant.valueOf ( this.active ) );
        builder.setAttribute ( intern ( this.prefix + ".requireAck" ), Variant.valueOf ( this.requireAkn ) );

        builder.setAttribute ( intern ( this.prefix + ".ackRequired" ), Variant.valueOf ( this.akn ) );
        builder.setAttribute ( intern ( this.prefix + ".state" ), Variant.valueOf ( this.state.toString () ) );

        builder.setAttribute ( intern ( this.prefix + ".unsafe" ), Variant.valueOf ( this.unsafe ) );

        Severity severity = getSeverity ();

        // be sure we don't have a null value
        severity = severity == null ? Severity.ERROR : severity;

        switch ( severity )
        {
            case INFORMATION:
                builder.setAttribute ( intern ( this.prefix + ".info" ), Variant.valueOf ( this.alarm ) );
                break;
            case WARNING:
                builder.setAttribute ( intern ( this.prefix + ".warning" ), Variant.valueOf ( this.alarm ) );
                break;
            case ERROR:
                builder.setAttribute ( intern ( this.prefix + ".alarm" ), Variant.valueOf ( this.alarm ) );
                break;
            case FATAL:
                builder.setAttribute ( intern ( this.prefix + ".error" ), Variant.valueOf ( this.alarm ) );
                break;
        }
    }

    @Override
    protected void buildMonitorAttributes ( final Map<String, Variant> attributes )
    {
        super.buildMonitorAttributes ( attributes );

        Severity severity = getSeverity ();

        // be sure we don't have a null value
        severity = severity == null ? Severity.ERROR : severity;

        attributes.put ( "severity", Variant.valueOf ( severity.name () ) );
    }

    protected String intern ( final String string )
    {
        return this.stringInterner.intern ( string );
    }

    protected WriteRequestResult handleProcessWrite ( final WriteRequest request )
    {
        if ( request.getAttributes () != null )
        {
            return handleAttributesWrite ( request );
        }
        return null;
    }

    protected WriteRequestResult handleAttributesWrite ( final WriteRequest request )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ( request.getAttributes () );
        final WriteAttributeResults result = new WriteAttributeResults ();

        simpleHandleAttributes ( attributes, result, request.getOperationParameters () );

        // remove result keys from request
        for ( final String attr : result.keySet () )
        {
            attributes.remove ( attr );
        }

        return new WriteRequestResult ( request.getValue (), attributes, result );
    }

    protected void simpleHandleAttributes ( final Map<String, Variant> attributes, final WriteAttributeResults result, final OperationParameters operationParameters )
    {
        final Map<String, String> configUpdate = new HashMap<String, String> ();

        handleConfigUpdate ( configUpdate, attributes, result );

        if ( !configUpdate.isEmpty () )
        {
            updateConfiguration ( configUpdate, operationParameters );
        }
    }

    private void updateConfiguration ( final Map<String, String> configUpdate, final OperationParameters operationParameters )
    {
        logger.info ( "Request to update configuration: {}", configUpdate );

        final String factoryId = getFactoryId ();
        final String configurationId = getConfigurationId ();

        logger.info ( "Directing update to: {}/{}", new Object[] { factoryId, configurationId } );

        if ( factoryId != null && configurationId != null )
        {
            try
            {
                Activator.getConfigAdmin ().updateConfiguration ( operationParameters.getUserInformation (), factoryId, configurationId, configUpdate, false );
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed to update configuration", e );
                throw new RuntimeException ( "Unable to update configuration", e );
            }
        }
    }

    @Override
    public synchronized void setRequireAkn ( final UserInformation userInformation, final boolean state )
    {
        super.setRequireAkn ( userInformation, state );
        this.requireAkn = state;
        reprocess ();
    }

    public synchronized void setSeverity ( final UserInformation userInformation, final Severity severity )
    {
        // FIXME: we should sent out a CFG event
        this.severity = severity;
        reprocess ();
    }

    @Override
    public synchronized void setActive ( final UserInformation userInformation, final boolean state )
    {
        super.setActive ( userInformation, state );
        this.active = state;
        reprocess ();
    }

    protected void reprocess ()
    {
        final MasterItem item = this.masterItem;
        if ( item != null )
        {
            item.reprocess ();
        }
    }

    protected void handleConfigUpdate ( final Map<String, String> configUpdate, final Map<String, Variant> attributes, final WriteAttributeResults result )
    {
        final Variant active = attributes.get ( this.prefix + ".active" );
        if ( active != null )
        {
            configUpdate.put ( "active", active.asBoolean () ? "true" : "false" );
            result.put ( intern ( this.prefix + ".active" ), WriteAttributeResult.OK );
        }

        final Variant requireAkn = attributes.get ( this.prefix + ".requireAck" );
        if ( requireAkn != null )
        {
            configUpdate.put ( "requireAck", requireAkn.asBoolean () ? "true" : "false" );
            result.put ( intern ( this.prefix + ".requireAck" ), WriteAttributeResult.OK );
        }
    }

    protected static Date toTimestamp ( final DataItemValue value )
    {
        if ( value == null )
        {
            return new Date ();
        }
        final Calendar c = value.getTimestamp ();
        if ( c == null )
        {
            return new Date ();
        }
        else
        {
            return c.getTime ();
        }
    }

    @Override
    protected void injectEventAttributes ( final EventBuilder builder )
    {
        super.injectEventAttributes ( builder );
        builder.attribute ( Event.Fields.MONITOR_TYPE, this.monitorType );
    }

    protected static boolean isDifferent ( final Object oldLimit, final Object newLimit )
    {
        if ( oldLimit == newLimit )
        {
            return false;
        }
        if ( oldLimit == null )
        {
            return true;
        }
        return !oldLimit.equals ( newLimit );
    }

}