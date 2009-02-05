/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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
 * The default validation strategy defines how item IDs are
 * validated. 
 * @author Jens Reimann
 *
 */
public enum ValidationStrategy
{
    /**
     * Perform a full check through all possible checks. If the item
     * does not exists and all factories reject creating the item
     * the item is considered "invalid".
     */
    FULL_CHECK,
    /**
     * Be permissive and grant everything. If the item does not exists
     * and all factories reject creating the item it is still considered
     * "currently unknown" and will be connected as "GRANTED" since it
     * might be accepted later.
     */
    GRANT_ALL
}
