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

package org.openscada.da.server.common.impl.stats;

import org.openscada.core.Variant;
import org.openscada.da.core.server.Session;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.impl.SessionCommon;

public interface HiveEventListener
{

    public abstract void sessionCreated ( SessionCommon session );

    public abstract void sessionDestroyed ( SessionCommon session );

    public abstract void itemRegistered ( DataItem item );

    public abstract void startWriteAttributes ( Session session, String itemId, int size );

    public abstract void startWrite ( Session session, String itemName, Variant value );

    public abstract void attributesChanged ( DataItem item, int size );

    public abstract void valueChanged ( DataItem item, Variant variant, boolean cache );

    public abstract void itemUnregistered ( DataItem item );

}
