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

import java.security.KeyStore.Builder;

import org.openscada.ae.MonitorStatus;
import org.openscada.ae.MonitorStatusInformation;
import org.openscada.ae.Severity;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;

import com.google.common.collect.Interner;

public class MonitorStateInjector
{
    private final String prefix;

    private MonitorStatus state;

    private boolean active;

    private boolean akn;

    private boolean unsafe;

    private boolean alarm;

    private Severity severity;

    private final Interner<String> stringInterner;

    public MonitorStateInjector ( final String prefix, final Interner<String> stringInterner )
    {
        this.prefix = prefix;
        this.stringInterner = stringInterner;
    }

    public void notifyStateChange ( final MonitorStatusInformation status )
    {
        // evaluate status bits for later use ... but only when updating
        this.state = status.getStatus ();
        this.active = this.state != MonitorStatus.INACTIVE;
        this.akn = this.state == MonitorStatus.NOT_AKN || this.state == MonitorStatus.NOT_OK_NOT_AKN;
        this.unsafe = this.state == MonitorStatus.UNSAFE;
        this.alarm = this.state == MonitorStatus.NOT_OK || this.state == MonitorStatus.NOT_OK_AKN || this.state == MonitorStatus.NOT_OK_NOT_AKN;
        this.severity = status.getSeverity ();
    }

    protected String intern ( final String value )
    {
        if ( this.stringInterner == null || value == null )
        {
            return value;
        }
        else
        {
            return this.stringInterner.intern ( value );
        }
    }

    /**
     * Inject attributes to the value after the value update has been performed using {@link #performDataUpdate(Builder)}
     * 
     * @param builder
     *            the builder to use for changing information
     */
    public void injectAttributes ( final DataItemValue.Builder builder )
    {
        builder.setAttribute ( intern ( this.prefix + ".active" ), Variant.valueOf ( this.active ) );

        builder.setAttribute ( intern ( this.prefix + ".ackRequired" ), Variant.valueOf ( this.akn ) );
        builder.setAttribute ( intern ( this.prefix + ".state" ), Variant.valueOf ( this.state ) );

        builder.setAttribute ( intern ( this.prefix + ".unsafe" ), Variant.valueOf ( this.unsafe ) );

        // be sure we don't have a null value
        final Severity severity = this.severity == null ? Severity.ALARM : this.severity;

        switch ( severity )
        {
            case INFORMATION:
                builder.setAttribute ( intern ( this.prefix + ".info" ), Variant.valueOf ( this.alarm ) );
                break;
            case WARNING:
                builder.setAttribute ( intern ( this.prefix + ".warning" ), Variant.valueOf ( this.alarm ) );
                break;
            case ALARM:
                builder.setAttribute ( intern ( this.prefix + ".alarm" ), Variant.valueOf ( this.alarm ) );
                break;
            case ERROR:
                builder.setAttribute ( intern ( this.prefix + ".error" ), Variant.valueOf ( this.alarm ) );
                break;
        }
    }
}