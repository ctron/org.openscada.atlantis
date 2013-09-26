/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.core;

import java.util.Set;

import org.eclipse.scada.da.data.IODirection;

/**
 * Data item information
 * <p>
 * Data items information objects must be equal on their name!
 * 
 * @author Jens Reimann
 */
public interface DataItemInformation
{
    public abstract Set<IODirection> getIODirection ();

    /**
     * Get the ID of the data item
     * <p>
     * Although the getter is called <em>name</em> it returns the <em>id</em>
     * since somewhere in the past "name" was renamed to "id".
     * 
     * @return the id of the data item
     */
    public abstract String getName ();
}
