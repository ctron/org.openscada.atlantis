/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common;

/**
 * Interface for suspendable items
 * <p>
 * If a DataItem implements this interface it may suspend value collection
 * when the {@link #suspend()} method is called and must restart the value collection
 * when {@link #wakeup()} is called.
 * <p>
 * Only the automatic value collection may be suspended. Read or write requests must
 * be processed at any time!
 * @author jens
 *
 */
public interface SuspendableItem
{
    /**
     * Called <em>before</em> the first listener is subscribed
     *
     */
    void wakeup ();
    /**
     * Called <em>after</em> the last listner is unsubscribed
     *
     */
    void suspend ();
}
