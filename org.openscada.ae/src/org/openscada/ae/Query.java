/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.ae;

/**
 * Must be automatically garbage collected if the {@link QueryListener} implementation
 * does not store the instance itself 
 * @author Jens Reimann
 * @author Jürgen Rose
 * @since 0.15.0
 */
public interface Query
{
    /**
     * Load more data
     * @param count the number of entries to load, must be greater than zero
     * @throws IllegalArgumentException if the count is negative or zero
     */
    public void loadMore ( int count );

    public void close ();
}
