/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.opc2.configuration;

public interface ItemSource
{
    /**
     * Activate processing of the item source.
     * <p>
     * The item source may only call
     * the listener methods when the activate method has been called. Also must
     * all listeners be registered with this item source it they want to received
     * events from the beginning. 
     */
    public void activate ();

    public void deactivate ();

    /**
     * Add a listener to the item source.
     * <p>
     * Be sure to register all listeners prior calling the {@link #activate()} method
     * @param listener the listener to add
     */
    public void addListener ( ItemSourceListener listener );

    public void removeListener ( ItemSourceListener listener );
}
