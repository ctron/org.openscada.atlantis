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

package org.openscada.ae;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.utils.lang.Immutable;

@Immutable
public class MonitorStatusInformation implements Serializable
{

    private static final long serialVersionUID = 6138369511783020510L;

    private final String id;

    private final MonitorStatus status;

    private final Date statusTimestamp;

    private final Variant value;

    private final String lastAknUser;

    private final Date lastAknTimestamp;

    private final Map<String, Variant> attributes;

    private final Date lastFailTimestamp;

    public MonitorStatusInformation ( final String id, final MonitorStatus status, final Date statusTimestamp, final Variant value, final Date lastAknTimestamp, final String lastAknUser, final Date lastFailTimestamp, final Map<String, Variant> attributes )
    {
        super ();

        this.id = id;
        this.status = status;
        this.statusTimestamp = statusTimestamp;
        this.value = value;
        this.lastAknTimestamp = lastAknTimestamp;
        this.lastAknUser = lastAknUser;

        // we might not have a lastFailTimestamp if we are communicating with old versions
        if ( lastFailTimestamp == null )
        {
            this.lastFailTimestamp = statusTimestamp;
        }
        else
        {
            this.lastFailTimestamp = lastFailTimestamp;
        }

        if ( attributes == null )
        {
            this.attributes = Collections.emptyMap ();
        }
        else
        {
            this.attributes = new HashMap<String, Variant> ( attributes );
        }

        if ( id == null )
        {
            throw new NullPointerException ( "'id' must not be null" );
        }
        if ( status == null )
        {
            throw new NullPointerException ( "'status' must not be null" );
        }
        if ( statusTimestamp == null )
        {
            throw new NullPointerException ( "'statusTimestamp' must not be null" );
        }
    }

    public String getId ()
    {
        return this.id;
    }

    public MonitorStatus getStatus ()
    {
        return this.status;
    }

    public Date getStatusTimestamp ()
    {
        return (Date)this.statusTimestamp.clone ();
    }

    public Date getLastFailTimestamp ()
    {
        return (Date)this.lastFailTimestamp.clone ();
    }

    public Variant getValue ()
    {
        return this.value;
    }

    public String getLastAknUser ()
    {
        return this.lastAknUser;
    }

    public Date getLastAknTimestamp ()
    {
        return (Date)this.lastAknTimestamp.clone ();
    }

    public Map<String, Variant> getAttributes ()
    {
        return Collections.unmodifiableMap ( this.attributes );
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.id == null ? 0 : this.id.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( ! ( obj instanceof MonitorStatusInformation ) )
        {
            return false;
        }
        final MonitorStatusInformation other = (MonitorStatusInformation)obj;
        if ( this.id == null )
        {
            if ( other.id != null )
            {
                return false;
            }
        }
        else if ( !this.id.equals ( other.id ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString ()
    {
        final StringBuilder sb = new StringBuilder ();

        sb.append ( this.id + "(" );
        sb.append ( ", id=" + this.id );
        sb.append ( ", status=" + this.status );
        sb.append ( ", timestamp=" + this.statusTimestamp );
        sb.append ( ")" );

        return sb.toString ();
    }

}
