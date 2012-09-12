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

import java.io.Serializable;

public final class PersistentInformation implements Serializable
{

    private static final long serialVersionUID = 1L;

    private Long lastAckTimestamp;

    private String lastAckUser;

    private Long lastFailTimestamp;

    public Long getLastAckTimestamp ()
    {
        return this.lastAckTimestamp;
    }

    public void setLastAckTimestamp ( final Long lastAckTimestamp )
    {
        this.lastAckTimestamp = lastAckTimestamp;
    }

    public String getLastAckUser ()
    {
        return this.lastAckUser;
    }

    public void setLastAckUser ( final String lastAckUser )
    {
        this.lastAckUser = lastAckUser;
    }

    public Long getLastFailTimestamp ()
    {
        return this.lastFailTimestamp;
    }

    public void setLastFailTimestamp ( final Long lastFailTimestamp )
    {
        this.lastFailTimestamp = lastFailTimestamp;
    }

    @Override
    public String toString ()
    {
        return String.format ( "[lastAckTimestamp: %s, lastAckUser: %s, lastFailTimestamp: %s]", this.lastAckTimestamp, this.lastAckUser, this.lastFailTimestamp );
    }

}