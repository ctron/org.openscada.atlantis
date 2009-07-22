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

package org.openscada.da.server.proxy.utils;

import java.util.regex.Pattern;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class ProxyUtils
{
    /**
     * @param itemId
     * @param separator
     * @param proxyPrefix
     * @param originalPrefix
     * @return name of original item
     */
    public static String originalItemId ( final String itemId, final String separator, final ProxyPrefixName proxyPrefix, final ProxyPrefixName originalPrefix )
    {
    	if ( originalPrefix == null || "".equals ( originalPrefix.getName() ) ) {
    		return itemId.replaceFirst ( Pattern.quote ( proxyPrefix.getName () + separator ), "" );
    	}
        return itemId.replaceFirst ( Pattern.quote ( proxyPrefix.getName () + separator ), originalPrefix.getName () + separator );
    }

    /**
     * @param itemId
     * @param separator
     * @param proxyPrefix
     * @param originalPrefix
     * @return name of item the way it is named in proxyserver
     */
    public static String proxyItemId ( final String itemId, final String separator, final ProxyPrefixName proxyPrefix, final ProxyPrefixName originalPrefix )
    {
    	if ( originalPrefix == null || "".equals ( originalPrefix.getName() ) ) {
    		return proxyPrefix.getName () + separator + itemId;
    	}
        return itemId.replaceFirst ( Pattern.quote ( originalPrefix.getName () + separator ), proxyPrefix.getName () + separator );
    }

    /**
     * checks if an item id matches the sub connection prefix and may be put into the proxy group
     * @param itemId
     * @param separator
     * @param originalPrefix
     * @return
     */
    public static boolean isOriginalItemForProxyGroup ( final String itemId, final String separator, final ProxyPrefixName originalPrefix )
    {
    	if ( originalPrefix == null || "".equals ( originalPrefix.getName() ) ) {
    		return true;
    	}
        return itemId.startsWith ( originalPrefix.getName () + separator );
    }
}
