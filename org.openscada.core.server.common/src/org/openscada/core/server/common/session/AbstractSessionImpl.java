/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.core.server.common.session;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openscada.sec.UserInformation;

public abstract class AbstractSessionImpl implements UserSession
{
    private final UserInformation userInformation;

    private final Map<String, String> properties;

    public AbstractSessionImpl ( final UserInformation userInformation, final Map<String, String> properties )
    {
        this.userInformation = userInformation;
        this.properties = new HashMap<String, String> ( properties );
    }

    public Map<String, String> getProperties ()
    {
        return Collections.unmodifiableMap ( this.properties );
    }

    public UserInformation getUserInformation ()
    {
        return this.userInformation;
    }
}
