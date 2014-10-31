/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
