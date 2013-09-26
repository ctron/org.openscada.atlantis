/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.eclipse.scada.core.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.scada.sec.UserInformation;
import org.eclipse.scada.sec.callback.CallbackHandler;
import org.eclipse.scada.utils.lang.Immutable;

/**
 * @since 1.1
 */
@Immutable
public class OperationParameters
{
    private final UserInformation userInformation;

    private final Map<String, String> properties;

    private final CallbackHandler callbackHandler;

    public OperationParameters ( final UserInformation userInformation, final Map<String, String> properties, final CallbackHandler callbackHandler )
    {
        this.userInformation = userInformation;
        this.properties = properties == null ? Collections.<String, String> emptyMap () : Collections.<String, String> unmodifiableMap ( new HashMap<String, String> ( properties ) );
        this.callbackHandler = callbackHandler;
    }

    public CallbackHandler getCallbackHandler ()
    {
        return this.callbackHandler;
    }

    public UserInformation getUserInformation ()
    {
        return this.userInformation;
    }

    public Map<String, String> getProperties ()
    {
        return this.properties;
    }

    @Override
    public String toString ()
    {
        return String.format ( "[OperationParameters - userInformation: %s, properties: %s, callbackHandler: %s]", this.userInformation, this.properties, this.callbackHandler );
    }

    public org.eclipse.scada.core.data.OperationParameters asData ()
    {
        final org.eclipse.scada.core.data.UserInformation ui = this.userInformation == null ? null : new org.eclipse.scada.core.data.UserInformation ( this.userInformation.getName () );
        return new org.eclipse.scada.core.data.OperationParameters ( ui, this.properties );
    }
}
