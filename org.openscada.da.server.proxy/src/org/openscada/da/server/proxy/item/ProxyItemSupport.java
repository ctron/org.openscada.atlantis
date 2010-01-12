/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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