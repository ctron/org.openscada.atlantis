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

package org.openscada.da.client.ngp;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverInformation implements org.openscada.core.client.DriverInformation
{

    private final static Logger logger = LoggerFactory.getLogger ( DriverInformation.class );

    @Override
    public Connection create ( final ConnectionInformation connectionInformation )
    {
        if ( connectionInformation.getSecondaryTarget () == null )
        {
            return null;
        }

        try
        {
            return new org.openscada.da.client.ngp.ConnectionImpl ( connectionInformation );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to create connection", e );
            return null;
        }
    }

    @Override
    public Class<?> getConnectionClass ()
    {
        return org.openscada.da.client.ngp.ConnectionImpl.class;
    }

    @Override
    public void validate ( final ConnectionInformation connectionInformation ) throws Throwable
    {
    }

}
