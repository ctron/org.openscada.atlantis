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

package org.openscada.da.client.dataitem.details.part;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.services.IDisposable;
import org.openscada.da.client.Connection;
import org.openscada.da.client.DataItem;

public interface DetailsPart extends IDisposable
{
    /**
     * create the details area
     * @param parent the parent composite
     */
    public void createPart ( Composite parent );

    /**
     * set to update the item instance
     * @param connection the connection of the item
     * @param item the data item
     */
    public void setDataItem ( Connection connection, DataItem item );
}
