/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.ae.client.net.operations;

import org.openscada.ae.net.ListMessage;
import org.openscada.ae.net.Messages;
import org.openscada.net.base.LongRunningController;
import org.openscada.net.mina.Messenger;
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningOperation;

public class ListOperationController
{
    private LongRunningController controller = null;

    public ListOperationController ( final Messenger connection )
    {
        this.controller = new LongRunningController ( connection, Messages.CC_CANCEL_OPERATION, Messages.CC_LIST_REPLY );
    }

    public void register ()
    {
        this.controller.register ();
    }

    public void unregister ()
    {
        this.controller.unregister ();
    }

    public LongRunningOperation start ( final LongRunningListener listener )
    {
        final ListMessage message = new ListMessage ();
        return this.controller.start ( message.toMessage (), listener );
    }

}
