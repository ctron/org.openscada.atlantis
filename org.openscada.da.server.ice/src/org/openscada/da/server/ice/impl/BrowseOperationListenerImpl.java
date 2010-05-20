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

package org.openscada.da.server.ice.impl;

import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.BrowseOperationListener;

public class BrowseOperationListenerImpl implements BrowseOperationListener
{
    private Throwable _error = null;

    private Entry[] _result = null;

    private boolean _completed = false;

    public synchronized void failure ( final Throwable throwable )
    {
        this._error = throwable;
        this._completed = true;
        notifyAll ();
    }

    public synchronized void success ( final Entry[] result )
    {
        this._result = result;
        this._completed = true;
        notifyAll ();
    }

    public Throwable getError ()
    {
        return this._error;
    }

    public Entry[] getResult ()
    {
        return this._result;
    }

    public synchronized void waitForCompletion () throws InterruptedException
    {
        if ( this._completed )
        {
            return;
        }

        wait ();
    }
}
