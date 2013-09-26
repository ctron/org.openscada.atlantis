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

package org.eclipse.scada.da.server.common;

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
