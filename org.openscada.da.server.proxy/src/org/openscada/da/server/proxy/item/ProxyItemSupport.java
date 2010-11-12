/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.proxy.item;

import org.openscada.da.server.proxy.utils.ProxyPrefixName;
import org.openscada.da.server.proxy.utils.ProxySubConnectionId;

public class ProxyItemSupport
{
    protected volatile ProxySubConnectionId currentConnection;

    protected String separator = ".";

    protected final ProxyPrefixName prefix;

    /**
     * This is the item Id of the proxy item
     */
    protected final String proxyItemId;

    public ProxyItemSupport ( final String separator, final ProxyPrefixName prefix, final ProxySubConnectionId currentConnection, final String proxyItemId )
    {
        this.separator = separator;
        this.prefix = prefix;
        this.currentConnection = currentConnection;
        this.proxyItemId = proxyItemId;
    }

    /**
     * Switch between connections
     * @param newConnection
     */
    public void switchTo ( final ProxySubConnectionId newConnection )
    {
        this.currentConnection = newConnection;
    }

    /**
     * @return id of proxy item
     */
    public String getItemId ()
    {
        return this.proxyItemId;
    }

}