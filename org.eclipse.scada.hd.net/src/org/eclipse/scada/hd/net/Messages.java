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

package org.eclipse.scada.hd.net;

public class Messages
{
    /**
     * Base command code for all HD messages
     */
    public static final int CC_HD_BASE = 0x00040000;

    /* query interface */

    public static final int CC_HD_CREATE_QUERY = CC_HD_BASE + 0x0001;

    public static final int CC_HD_CLOSE_QUERY = CC_HD_BASE + 0x0002;

    public static final int CC_HD_CHANGE_QUERY_PARAMETERS = CC_HD_BASE + 0x0003;

    public static final int CC_HD_UPDATE_QUERY_STATUS = CC_HD_BASE + 0x0004;

    public static final int CC_HD_UPDATE_QUERY_DATA = CC_HD_BASE + 0x0005;

    public static final int CC_HD_UPDATE_QUERY_PARAMETERS = CC_HD_BASE + 0x0006;

    /* List interface */

    public static final int CC_HD_START_LIST = CC_HD_BASE + 0x0011;

    public static final int CC_HD_STOP_LIST = CC_HD_BASE + 0x0012;

    public static final int CC_HD_LIST_UPDATE = CC_HD_BASE + 0x0013;

}
