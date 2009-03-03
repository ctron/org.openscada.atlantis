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
