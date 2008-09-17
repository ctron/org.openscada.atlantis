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
