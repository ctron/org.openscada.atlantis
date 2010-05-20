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

package org.openscada.da.client.net.operations;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.net.handler.Messages;
import org.openscada.da.net.handler.WriteAttributesOperation;
import org.openscada.net.base.LongRunningController;
import org.openscada.net.base.data.Message;
import org.openscada.net.mina.Messenger;
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningOperation;

public class WriteAttributesOperationController
{
    private LongRunningController _controller = null;

    public WriteAttributesOperationController ( final Messenger connection )
    {
        this._controller = new LongRunningController ( connection, Messages.CC_WRITE_ATTRIBUTES_OPERATION_RESULT );
    }

    public void register ()
    {
        this._controller.register ();
    }

    public void unregister ()
    {
        this._controller.unregister ();
    }

    public LongRunningOperation start ( final String itemName, final Map<String, Variant> attributes, final LongRunningListener listener )
    {
        final Message message = WriteAttributesOperation.createRequest ( itemName, attributes );

        return this._controller.start ( message, listener );
    }

}
