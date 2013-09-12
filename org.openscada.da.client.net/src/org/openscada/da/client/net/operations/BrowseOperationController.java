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

package org.openscada.da.client.net.operations;

import org.eclipse.scada.utils.exec.LongRunningListener;
import org.eclipse.scada.utils.exec.LongRunningOperation;
import org.openscada.da.net.handler.ListBrowser;
import org.openscada.da.net.handler.Messages;
import org.openscada.net.base.LongRunningController;
import org.openscada.net.base.data.Message;
import org.openscada.net.mina.Messenger;

public class BrowseOperationController
{
    private LongRunningController controller = null;

    public BrowseOperationController ( final Messenger connection )
    {
        this.controller = new LongRunningController ( connection, Messages.CC_BROWSER_LIST_RES );
    }

    public void register ()
    {
        this.controller.register ();
    }

    public void unregister ()
    {
        this.controller.unregister ();
    }

    public LongRunningOperation start ( final String[] location, final LongRunningListener listener )
    {
        final Message message = ListBrowser.createRequest ( location );

        return this.controller.start ( message, listener );
    }

}
