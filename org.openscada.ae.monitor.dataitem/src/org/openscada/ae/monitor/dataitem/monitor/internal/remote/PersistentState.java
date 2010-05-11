/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

import java.io.Serializable;
import java.util.Date;

import org.openscada.ae.ConditionStatus;

public class PersistentState implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String lastAckUser;

    private ConditionStatus state;

    private Date timestamp;

    private Date ackTimestamp;

    public PersistentState ()
    {
        this.state = ConditionStatus.INIT;
    }

    public String getLastAckUser ()
    {
        return this.lastAckUser;
    }

    public void setLastAckUser ( final String lastAckUser )
    {
        this.lastAckUser = lastAckUser;
    }

    public ConditionStatus getState ()
    {
        return this.state;
    }

    public void setState ( final ConditionStatus state )
    {
        this.state = state;
    }

    public Date getAckTimestamp ()
    {
        return this.ackTimestamp;
    }

    public Date getTimestamp ()
    {
        return this.timestamp;
    }

    public void setAckTimestamp ( final Date ackTimestamp )
    {
        this.ackTimestamp = ackTimestamp;
    }

    public void setTimestamp ( final Date timestamp )
    {
        this.timestamp = timestamp;
    }

    @Override
    public String toString ()
    {
        return String.format ( "[PersistentState - State: %s, Timestamp: %s, LastAckUser: %s, LastAckTimestamp: %s", this.state, this.timestamp, this.lastAckUser, this.ackTimestamp );
    }
}
