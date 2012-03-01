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

package org.openscada.da.net.handler;

import org.openscada.da.core.OperationParameters;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.sec.UserInformation;

public class Operation
{
    public static final String FIELD_USER = "user";

    public static final String FIELD_PASSWORD = "password";

    public static final String FIELD_OPERATION_PARAMETERS = "operation-parameters";

    public static OperationParameters convertOperationParameters ( final Value value )
    {
        if ( value == null )
        {
            return null;
        }
        if ( ! ( value instanceof MapValue ) )
        {
            return null;
        }
        final MapValue mapValue = (MapValue)value;

        final String user = mapValue.get ( FIELD_USER ) != null ? mapValue.get ( FIELD_USER ).toString () : null;
        final String password = mapValue.get ( FIELD_PASSWORD ) != null ? mapValue.get ( FIELD_PASSWORD ).toString () : null;

        return new OperationParameters ( new UserInformation ( user, password ) );
    }

    public static void encodeOperationParameters ( final OperationParameters operationParameters, final Message message )
    {
        if ( operationParameters != null )
        {
            final MapValue parameters = new MapValue ( 2 );
            message.getValues ().put ( FIELD_OPERATION_PARAMETERS, parameters );
            if ( operationParameters.getUserInformation () != null && operationParameters.getUserInformation ().getName () != null )
            {
                parameters.put ( FIELD_USER, new StringValue ( operationParameters.getUserInformation ().getName () ) );
            }
            if ( operationParameters.getUserInformation () != null && operationParameters.getUserInformation ().getPassword () != null )
            {
                parameters.put ( FIELD_PASSWORD, new StringValue ( operationParameters.getUserInformation ().getPassword () ) );
            }
        }
    }
}
