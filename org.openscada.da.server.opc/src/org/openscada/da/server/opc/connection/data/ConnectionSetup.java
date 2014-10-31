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

package org.openscada.da.server.opc.connection.data;

import org.openscada.opc.lib.common.ConnectionInformation;

public class ConnectionSetup
{
    private ConnectionInformation connectionInformation = null;

    private boolean initialConnect = true;

    private AccessMethod accessMethod = AccessMethod.SYNC;

    private int updateRate = 500;

    private boolean flatBrowser = true;

    private boolean treeBrowser = true;

    private String deviceTag;

    private String itemIdPrefix;

    private boolean ignoreTimestampOnlyChange = false;

    private int reconnectDelay;

    private short qualityErrorIfLessThen = 192;

    public ConnectionSetup ()
    {
        super ();
    }

    public ConnectionSetup ( final ConnectionInformation connectionInformation )
    {
        super ();
        this.connectionInformation = connectionInformation;
    }

    public int getReconnectDelay ()
    {
        return this.reconnectDelay;
    }

    public void setReconnectDelay ( final int reconnectDelay )
    {
        this.reconnectDelay = reconnectDelay;
    }

    public boolean isIgnoreTimestampOnlyChange ()
    {
        return this.ignoreTimestampOnlyChange;
    }

    public void setIgnoreTimestampOnlyChange ( final boolean ignoreTimestampOnlyChange )
    {
        this.ignoreTimestampOnlyChange = ignoreTimestampOnlyChange;
    }

    public AccessMethod getAccessMethod ()
    {
        return this.accessMethod;
    }

    public void setAccessMethod ( final AccessMethod accessMethod )
    {
        this.accessMethod = accessMethod;
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return this.connectionInformation;
    }

    public void setConnectionInformation ( final ConnectionInformation connectionInformation )
    {
        this.connectionInformation = connectionInformation;
    }

    public boolean isInitialConnect ()
    {
        return this.initialConnect;
    }

    public void setInitialConnect ( final boolean initialConnect )
    {
        this.initialConnect = initialConnect;
    }

    public int getUpdateRate ()
    {
        return this.updateRate;
    }

    public void setUpdateRate ( final int refreshTimeout )
    {
        this.updateRate = refreshTimeout;
    }

    public boolean isFlatBrowser ()
    {
        return this.flatBrowser;
    }

    public void setFlatBrowser ( final boolean flatBrowser )
    {
        this.flatBrowser = flatBrowser;
    }

    public boolean isTreeBrowser ()
    {
        return this.treeBrowser;
    }

    public void setTreeBrowser ( final boolean treeBrowser )
    {
        this.treeBrowser = treeBrowser;
    }

    public String getDeviceTag ()
    {
        return this.deviceTag;
    }

    public void setDeviceTag ( final String deviceTag )
    {
        this.deviceTag = deviceTag;
    }

    public String getItemIdPrefix ()
    {
        return this.itemIdPrefix;
    }

    public void setItemIdPrefix ( final String itemIdPrefix )
    {
        this.itemIdPrefix = itemIdPrefix;
    }

    public short getQualityErrorIfLessThen ()
    {
        return this.qualityErrorIfLessThen;
    }

    public void setQualityErrorIfLessThen ( final short qualityErrorIfLessThen )
    {
        this.qualityErrorIfLessThen = qualityErrorIfLessThen;
    }

}
