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

package org.openscada.da.server.common;

/**
 * An interface for items that are interested if currently a listener is set or not.
 * <br/>
 * Note that this interface must be fed by someone. It is not automatically fed by the Hive
 * anymore since the item should take care itself of this state. See {@link DataItemBase} for
 * a class supporting this interface.
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 * @see DataItemBase
 */
public interface SuspendableDataItem
{
    /**
     * Called when the listener is set from a valid listener to <code>null</code>
     *
     */
    public abstract void suspend ();

    /**
     * Called when the listener is set from <code>null</code> to a valid listener
     *
     */
    public abstract void wakeup ();
}
