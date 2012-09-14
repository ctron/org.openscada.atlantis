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

package org.openscada.ae.monitor.datasource;

import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ae.Severity;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.master.MasterItem;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Interner;

public abstract class AbstractDemotingMasterItemMonitor extends AbstractMasterItemMonitor
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractDemotingMasterItemMonitor.class );

    private static final String CONTEXT_KEY_DEMOTE_TO_SEVERITY_SUFFIX = "severity";

    private static final String CONTEXT_KEY_DEMOTE_TO_OK_SUFFIX = "ok";

    private static final String CONTEXT_KEY_DEMOTE_TO_INFORMATION_SUFFIX = "information";

    private static final String CONTEXT_KEY_DEMOTE_TO_WARNING_SUFFIX = "warning";

    private static final String CONTEXT_KEY_DEMOTE_TO_ALARM_SUFFIX = "alarm";

    private static final String CONTEXT_KEY_DEMOTE_TO_ACK_SUFFIX = "ack";

    private Severity severityLimit;

    private String demotePrefix;

    private boolean demoteAck;

    public AbstractDemotingMasterItemMonitor ( final BundleContext context, final Executor executor, final Interner<String> stringInterner, final ObjectPoolTracker<MasterItem> poolTracker, final EventProcessor eventProcessor, final String id, final String factoryId, final String prefix, final String defaultMonitorType )
    {
        super ( context, executor, stringInterner, poolTracker, eventProcessor, id, factoryId, prefix, defaultMonitorType );
    }

    @Override
    protected DataItemValue handleDataUpdate ( final Map<String, Object> context, final DataItemValue value )
    {
        this.severityLimit = extractSeverity ( context );
        this.demoteAck = false;

        if ( this.demotePrefix != null )
        {
            if ( isKey ( CONTEXT_KEY_DEMOTE_TO_OK_SUFFIX, context ) )
            {
                this.severityLimit = null;
            }
            else if ( isKey ( CONTEXT_KEY_DEMOTE_TO_INFORMATION_SUFFIX, context ) )
            {
                this.severityLimit = Severity.INFORMATION;
            }
            else if ( isKey ( CONTEXT_KEY_DEMOTE_TO_WARNING_SUFFIX, context ) )
            {
                this.severityLimit = Severity.WARNING;
            }
            else if ( isKey ( CONTEXT_KEY_DEMOTE_TO_ALARM_SUFFIX, context ) )
            {
                this.severityLimit = Severity.ALARM;
            }
            if ( isKey ( CONTEXT_KEY_DEMOTE_TO_ACK_SUFFIX, context ) )
            {
                this.demoteAck = true;
            }
        }

        return super.handleDataUpdate ( context, value );
    }

    private Severity extractSeverity ( final Map<String, Object> context )
    {
        if ( this.demotePrefix == null )
        {
            return Severity.ERROR;
        }

        final Object value = context.get ( this.demotePrefix + "." + CONTEXT_KEY_DEMOTE_TO_SEVERITY_SUFFIX );
        if ( value == null )
        {
            return Severity.ERROR;
        }

        try
        {
            return Severity.valueOf ( value.toString () );
        }
        catch ( final Exception e )
        {
            return Severity.ERROR;
        }
    }

    private boolean isKey ( final String suffix, final Map<String, Object> context )
    {
        final Object value = context.get ( this.demotePrefix + "." + suffix );
        if ( value == null )
        {
            return false;
        }
        return true;
    }

    @Override
    public synchronized void update ( final UserInformation userInformation, final Map<String, String> properties ) throws Exception
    {
        super.update ( userInformation, properties );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( properties );
        this.demotePrefix = cfg.getString ( "demote.prefix", null );
    }

    @Override
    protected void setFailure ( final Variant value, final Long valueTimestamp, final Severity severity, final boolean requireAck )
    {
        final Severity result = demoteSeverity ( severity );
        if ( result == null )
        {
            setOk ( value, valueTimestamp );
        }
        else
        {
            super.setFailure ( value, valueTimestamp, result, demoteAck ( requireAck ) );
        }
    }

    protected boolean demoteAck ( final boolean requireAck )
    {
        if ( this.demotePrefix == null )
        {
            return requireAck;
        }

        if ( this.demoteAck )
        {
            return false;
        }
        return requireAck;
    }

    protected Severity demoteSeverity ( final Severity severity )
    {
        if ( this.demotePrefix == null || severity == null )
        {
            // we are not active
            return severity;
        }

        if ( this.severityLimit == null )
        {
            return null;
        }

        final int ordinal = Math.min ( this.severityLimit.ordinal (), severity.ordinal () );

        logger.debug ( "Demoted severity from {} to {}", severity.ordinal (), ordinal );

        return Severity.values ()[ordinal];
    }

}
