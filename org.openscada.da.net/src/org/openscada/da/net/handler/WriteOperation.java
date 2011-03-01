/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import org.openscada.core.Variant;
import org.openscada.core.net.MessageHelper;
import org.openscada.da.core.OperationParameters;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.utils.lang.Holder;

public class WriteOperation extends Operation
{

    public static Message create ( final String itemName, final Variant value, final OperationParameters operationParameters )
    {
        final Message message = new Message ( Messages.CC_WRITE_OPERATION );

        message.getValues ().put ( "item-name", new StringValue ( itemName ) );
        message.getValues ().put ( "value", MessageHelper.variantToValue ( value ) );

        encodeOperationParameters ( operationParameters, message );

        return message;
    }

    public static void parse ( final Message message, final Holder<String> itemName, final Holder<Variant> value, final Holder<OperationParameters> operationParameters )
    {
        // FIXME: handle missing item name

        final MapValue values = message.getValues ();

        itemName.value = values.get ( "item-name" ).toString ();

        value.value = MessageHelper.valueToVariant ( values.get ( "value" ), Variant.NULL );

        operationParameters.value = convertOperationParameters ( values.get ( FIELD_OPERATION_PARAMETERS ) );
    }
}
