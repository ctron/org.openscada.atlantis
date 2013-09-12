/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.client.net.operations;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.utils.exec.LongRunningListener;
import org.eclipse.scada.utils.exec.LongRunningOperation;
import org.openscada.core.data.OperationParameters;
import org.openscada.da.net.handler.Messages;
import org.openscada.net.base.LongRunningController;
import org.openscada.net.base.data.Message;
import org.openscada.net.mina.Messenger;

public class WriteOperationController
{
    private LongRunningController controller = null;

    public WriteOperationController ( final Messenger connection )
    {
        this.controller = new LongRunningController ( connection, Messages.CC_WRITE_OPERATION_RESULT );
    }

    public void register ()
    {
        this.controller.register ();
    }

    public void unregister ()
    {
        this.controller.unregister ();
    }

    public LongRunningOperation start ( final String itemName, final Variant value, final OperationParameters operationParameters, final LongRunningListener listener )
    {
        final Message message = org.openscada.da.net.handler.WriteOperation.create ( itemName, value, operationParameters );

        return this.controller.start ( message, listener );
    }

}
