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

package org.openscada.ae.monitor.script;

import org.openscada.ae.Severity;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;

public class ScriptMonitorResult
{
    public static final ScriptMonitorResult UNSAFE;

    public static final ScriptMonitorResult INACTIVE;

    static
    {
        UNSAFE = new ScriptMonitorResult ();
        UNSAFE.monitorStatus = State.UNSAFE;

        INACTIVE = new ScriptMonitorResult ();
        INACTIVE.monitorStatus = State.INACTIVE;
    }

    State monitorStatus;

    Variant value;

    Long valueTimestamp;

    Severity severity;

    Boolean requireAck;

    public ScriptMonitorResult ()
    {
    }

    public ScriptMonitorResult ( final Variant value, final Long valueTimestamp )
    {
        this.monitorStatus = State.OK;
        this.value = value;
        this.valueTimestamp = valueTimestamp;
    }

    public ScriptMonitorResult ( final Variant value, final Long valueTimestamp, final Severity severity, final boolean requireAck )
    {
        this.monitorStatus = State.FAILURE;
        this.value = value;
        this.valueTimestamp = valueTimestamp;
        this.severity = severity;
        this.requireAck = requireAck;
    }

    public static class OkBuilder
    {
        public static final OkBuilder INSTANCE = new OkBuilder ();

        public ScriptMonitorResult build ( final DataItemValue value )
        {
            final Variant timestamp = value.getAttributes ().get ( "timestamp" );
            return new ScriptMonitorResult ( value.getValue (), timestamp == null ? null : timestamp.asLong ( null ) );
        }

        public ScriptMonitorResult build ( final Variant value, final Long valueTimestamp )
        {
            return new ScriptMonitorResult ( value, valueTimestamp );
        }
    }

    public static class FailureBuilder
    {
        public static final FailureBuilder INSTANCE = new FailureBuilder ();

        public ScriptMonitorResult build ( final DataItemValue value, final Severity severity, final boolean requireAck )
        {
            final Variant timestamp = value.getAttributes ().get ( "timestamp" );
            return new ScriptMonitorResult ( value.getValue (), timestamp == null ? null : timestamp.asLong ( null ), severity, requireAck );
        }

        public ScriptMonitorResult build ( final Variant value, final Long valueTimestamp, final Severity severity, final boolean requireAck )
        {
            return new ScriptMonitorResult ( value, valueTimestamp, severity, requireAck );
        }
    }

    public State getMonitorStatus ()
    {
        return this.monitorStatus;
    }

    public void setMonitorStatus ( final State monitorStatus )
    {
        this.monitorStatus = monitorStatus;
    }

    public Variant getValue ()
    {
        return this.value;
    }

    public void setValue ( final Variant value )
    {
        this.value = value;
    }

    public Long getValueTimestamp ()
    {
        return this.valueTimestamp;
    }

    public void setValueTimestamp ( final Long valueTimestamp )
    {
        this.valueTimestamp = valueTimestamp;
    }

    public Severity getSeverity ()
    {
        return this.severity;
    }

    public void setSeverity ( final Severity severity )
    {
        this.severity = severity;
    }

    public Boolean getRequireAck ()
    {
        return this.requireAck;
    }

    public void setRequireAck ( final Boolean requireAck )
    {
        this.requireAck = requireAck;
    }

    @Override
    public String toString ()
    {
        return String.format ( "[%s: %s - %s - %s - %s]", this.monitorStatus, this.severity, this.value, this.valueTimestamp, this.requireAck );
    }

}