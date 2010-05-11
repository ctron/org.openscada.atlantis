/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.ae.net;

public class Messages
{
    /**
     * Base command code for all A&E messages
     */
    public static final int CC_AE_BASE = 0x00030000;

    public static final int CC_SUBSCRIBE_EVENT_POOL = CC_AE_BASE + 0x0001;

    public static final int CC_UNSUBSCRIBE_EVENT_POOL = CC_AE_BASE + 0x0002;

    public static final int CC_EVENT_POOL_STATUS = CC_AE_BASE + 0x0003;

    public static final int CC_EVENT_POOL_DATA = CC_AE_BASE + 0x0004;

    public static final int CC_SUBSCRIBE_CONDITIONS = CC_AE_BASE + 0x0011;

    public static final int CC_UNSUBSCRIBE_CONDITIONS = CC_AE_BASE + 0x0012;

    public static final int CC_CONDITIONS_STATUS = CC_AE_BASE + 0x0013;

    public static final int CC_CONDITIONS_DATA = CC_AE_BASE + 0x0014;

    public static final int CC_CONDITION_AKN = CC_AE_BASE + 0x0015;

    public static final int CC_BROWSER_UPDATE = CC_AE_BASE + 0x0021;

    public static final int CC_QUERY_CREATE = CC_AE_BASE + 0x0031;

    public static final int CC_QUERY_CLOSE = CC_AE_BASE + 0x0032;

    public static final int CC_QUERY_STATUS_CHANGED = CC_AE_BASE + 0x0033;

    public static final int CC_QUERY_DATA = CC_AE_BASE + 0x0034;

    public static final int CC_QUERY_LOAD_MORE = CC_AE_BASE + 0x0035;

}
