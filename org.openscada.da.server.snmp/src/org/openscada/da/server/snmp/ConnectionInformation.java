/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.da.server.snmp;

public class ConnectionInformation implements Cloneable
{
    public static enum Version
    {
        V1,
        V2C,
        V3,
    };

    private Version version = Version.V2C;

    private String name;

    private String address;

    private String community;

    private String limitToOid;

    private int retries = 1;

    private long timeout = 5000; // 5sec

    public ConnectionInformation ( final Version version, final String name )
    {
        this.version = version;
        this.name = name;
    }

    public ConnectionInformation ( final ConnectionInformation other )
    {
        this.address = other.address;
        this.name = other.name;
        this.version = other.version;
        this.community = other.community;
        this.limitToOid = other.limitToOid;
        this.retries = other.retries;
        this.timeout = other.timeout;
    }

    public String getAddress ()
    {
        return this.address;
    }

    public void setAddress ( final String address )
    {
        this.address = address;
    }

    @Override
    public Object clone ()
    {
        return new ConnectionInformation ( this );
    }

    public String getName ()
    {
        return this.name;
    }

    public void setName ( final String name )
    {
        this.name = name;
    }

    public String getCommunity ()
    {
        return this.community;
    }

    public void setCommunity ( final String community )
    {
        this.community = community;
    }

    public Version getVersion ()
    {
        return this.version;
    }

    public void setVersion ( final Version version )
    {
        this.version = version;
    }

    public String getLimitToOid ()
    {
        return limitToOid;
    }

    public void setLimitToOid ( final String limitToOid )
    {
        this.limitToOid = limitToOid;
    }

    public void setRetries ( int retries )
    {
        this.retries = retries;
    }

    public int getRetries ()
    {
        return retries;
    }

    public void setTimeout ( long timeout )
    {
        this.timeout = timeout;
    }

    public long getTimeout ()
    {
        return timeout;
    }
}
