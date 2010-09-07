/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

import java.io.Serializable;
import java.util.Date;

import org.openscada.core.Variant;

public class StateInformation implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static enum State
    {
        OK,
        FAILED,
        UNSAFE;
    }

    private Boolean requireAck;

    private Boolean active;

    private State state;

    private Variant value;

    private Date timestamp;

    private String lastAckUser;

    private Date lastAckTimestamp;

    private Date lastFailTimestamp;

    public StateInformation ()
    {
    }

    public StateInformation ( final StateInformation information )
    {
        this.requireAck = information.requireAck;
        this.active = information.active;
        this.state = information.state;
        this.value = information.value;
        if ( information.timestamp != null )
        {
            this.timestamp = (Date)information.timestamp.clone ();
        }
        this.lastAckUser = information.lastAckUser;
        if ( information.lastAckTimestamp != null )
        {
            this.lastAckTimestamp = (Date)information.lastAckTimestamp.clone ();
        }
        if ( information.lastFailTimestamp != null )
        {
            this.lastFailTimestamp = (Date)information.lastFailTimestamp.clone ();
        }
    }

    public StateInformation apply ( final StateInformation information )
    {
        final StateInformation newInformation = new StateInformation ( this );
        if ( information.active != null )
        {
            newInformation.active = information.active;
        }
        if ( information.requireAck != null )
        {
            newInformation.requireAck = information.requireAck;
        }
        if ( information.state != null )
        {
            newInformation.state = information.state;
        }
        if ( information.value != null )
        {
            newInformation.value = information.value;
        }
        if ( information.timestamp != null )
        {
            newInformation.timestamp = (Date)information.timestamp.clone ();
        }
        if ( information.lastAckUser != null )
        {
            newInformation.lastAckUser = information.lastAckUser;
        }
        if ( information.lastAckTimestamp != null )
        {
            newInformation.lastAckTimestamp = information.lastAckTimestamp;
        }
        if ( information.lastFailTimestamp != null )
        {
            newInformation.lastFailTimestamp = information.lastFailTimestamp;
        }

        return newInformation;
    }

    public Date getLastFailTimestamp ()
    {
        return this.lastFailTimestamp;
    }

    public void setLastFailTimestamp ( final Date lastFailTimestamp )
    {
        this.lastFailTimestamp = lastFailTimestamp;
    }

    public Boolean getRequireAck ()
    {
        return this.requireAck;
    }

    public void setRequireAck ( final Boolean requireAck )
    {
        this.requireAck = requireAck;
    }

    public Boolean getActive ()
    {
        return this.active;
    }

    public void setActive ( final Boolean active )
    {
        this.active = active;
    }

    public State getState ()
    {
        return this.state;
    }

    public void setState ( final State state )
    {
        this.state = state;
    }

    public Variant getValue ()
    {
        return this.value;
    }

    public void setValue ( final Variant value )
    {
        this.value = value;
    }

    public Date getTimestamp ()
    {
        return this.timestamp;
    }

    public void setTimestamp ( final Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public String getLastAckUser ()
    {
        return this.lastAckUser;
    }

    public void setLastAckUser ( final String lastAckUser )
    {
        this.lastAckUser = lastAckUser;
    }

    public Date getLastAckTimestamp ()
    {
        return this.lastAckTimestamp;
    }

    public void setLastAckTimestamp ( final Date lackAckTimestamp )
    {
        this.lastAckTimestamp = lackAckTimestamp;
    }

    @Override
    public String toString ()
    {
        final StringBuilder sb = new StringBuilder ();

        sb.append ( "State: " );
        sb.append ( this.state );
        sb.append ( ", Active: " );
        sb.append ( this.active );
        sb.append ( ", requireAck: " );
        sb.append ( this.requireAck );
        sb.append ( ", value: " );
        sb.append ( this.value );
        sb.append ( ", timestamp: " );
        sb.append ( this.timestamp );
        sb.append ( ", lastFailTimestamp: " );
        sb.append ( this.lastFailTimestamp );
        sb.append ( ", lastAckTimestamp: " );
        sb.append ( this.lastAckTimestamp );
        sb.append ( ", lastAckUser: " );
        sb.append ( this.lastAckUser );

        return sb.toString ();
    }
}
