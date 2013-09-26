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

package org.eclipse.scada.hd.client.net;

import org.eclipse.scada.core.ConnectionInformation;
import org.eclipse.scada.core.client.Connection;

public class DriverInformationImpl implements org.eclipse.scada.core.client.DriverInformation
{
    public Connection create ( final ConnectionInformation connectionInformation )
    {
        if ( connectionInformation.getSecondaryTarget () == null )
        {
            return null;
        }

        return new org.eclipse.scada.hd.client.net.ConnectionImpl ( connectionInformation );
    }

    public Class<?> getConnectionClass ()
    {
        return org.eclipse.scada.hd.client.net.ConnectionImpl.class;
    }

    public void validate ( final ConnectionInformation connectionInformation ) throws Throwable
    {
    }

}
