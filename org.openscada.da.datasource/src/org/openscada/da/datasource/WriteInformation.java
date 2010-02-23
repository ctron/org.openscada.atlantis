/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.datasource;

import org.openscada.sec.UserInformation;
import org.openscada.utils.lang.Immutable;

/**
 * This class holds additional information for write requests
 * @author Jens Reimann
 * @since 0.15.0
 */
@Immutable
public class WriteInformation
{
    private final UserInformation userInformation;

    public WriteInformation ( final UserInformation userInformation )
    {
        this.userInformation = userInformation;
    }

    /**
     * Return the user information for this write request or <code>null</code>
     * if there is no user information available
     * @return the user information
     */
    public UserInformation getUserInformation ()
    {
        return this.userInformation;
    }
}
