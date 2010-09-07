/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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
