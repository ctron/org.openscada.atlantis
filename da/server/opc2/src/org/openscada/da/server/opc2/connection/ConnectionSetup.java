/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.server.opc2.connection;

import org.openscada.opc.lib.common.ConnectionInformation;

public class ConnectionSetup
{
    private ConnectionInformation _connectionInformation = null;
    private boolean _initialConnect = true;
    private AccessMethod _accessMethod = AccessMethod.ASYNC20;
    private int _refreshTimeout = 500;
    private boolean _flatBrowser = true;
    private boolean _treeBrowser = true;
    private String deviceTag = null;
    private String itemIdPrefix = null;
    private String fileSourceUri = null;

    public ConnectionSetup ()
    {
        super ();
    }

    public ConnectionSetup ( ConnectionInformation connectionInformation )
    {
        super ();
        _connectionInformation = connectionInformation;
    }

    public AccessMethod getAccessMethod ()
    {
        return _accessMethod;
    }

    public void setAccessMethod ( AccessMethod accessMethod )
    {
        _accessMethod = accessMethod;
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return _connectionInformation;
    }

    public void setConnectionInformation ( ConnectionInformation connectionInformation )
    {
        _connectionInformation = connectionInformation;
    }

    public boolean isInitialConnect ()
    {
        return _initialConnect;
    }

    public void setInitialConnect ( boolean initialConnect )
    {
        _initialConnect = initialConnect;
    }

    public int getRefreshTimeout ()
    {
        return _refreshTimeout;
    }

    public void setRefreshTimeout ( int refreshTimeout )
    {
        _refreshTimeout = refreshTimeout;
    }

    public boolean isFlatBrowser ()
    {
        return _flatBrowser;
    }

    public void setFlatBrowser ( boolean flatBrowser )
    {
        _flatBrowser = flatBrowser;
    }

    public boolean isTreeBrowser ()
    {
        return _treeBrowser;
    }

    public void setTreeBrowser ( boolean treeBrowser )
    {
        _treeBrowser = treeBrowser;
    }

    public String getDeviceTag ()
    {
        return deviceTag;
    }

    public void setDeviceTag ( String deviceTag )
    {
        this.deviceTag = deviceTag;
    }

    public String getItemIdPrefix ()
    {
        return itemIdPrefix;
    }

    public void setItemIdPrefix ( String itemIdPrefix )
    {
        this.itemIdPrefix = itemIdPrefix;
    }

    public String getFileSourceUri ()
    {
        return fileSourceUri;
    }

    public void setFileSourceUri ( String fileSourceUri )
    {
        this.fileSourceUri = fileSourceUri;
    }

}
