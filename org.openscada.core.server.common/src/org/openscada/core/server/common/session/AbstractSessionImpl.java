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

package org.openscada.core.server.common.session;

import java.util.Properties;

import org.openscada.sec.UserInformation;

public abstract class AbstractSessionImpl implements UserSession
{
    private final UserInformation userInformation;

    private final Properties properties;

    public AbstractSessionImpl ( final UserInformation userInformation, final Properties properties )
    {
        this.userInformation = userInformation;
        this.properties = properties;
    }

    public Properties getProperties ()
    {
        return new Properties ( this.properties );
    }

    public UserInformation getUserInformation ()
    {
        return this.userInformation;
    }
}
