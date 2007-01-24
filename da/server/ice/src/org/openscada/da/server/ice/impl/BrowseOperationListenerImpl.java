package org.openscada.da.server.ice.impl;

import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.BrowseOperationListener;

public class BrowseOperationListenerImpl implements BrowseOperationListener
{
    private Throwable _error = null;
    private Entry[] _result = null;
    private boolean _completed = false;
    
    public synchronized void failure ( Throwable throwable )
    {
        _error = throwable;
        _completed = true;
        notifyAll ();
    }

    public synchronized void success ( Entry[] result )
    {
        _result = result;
        _completed = true;
        notifyAll ();
    }

    public Throwable getError ()
    {
        return _error;
    }

    public Entry[] getResult ()
    {
        return _result;
    }
    
    public synchronized void waitForCompletion () throws InterruptedException
    {
        if ( _completed )
            return;

        wait ();
    }
}
