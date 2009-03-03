package org.openscada.da.client;

import org.openscada.da.core.browser.Entry;

public interface BrowseOperationCallback
{
    public void complete ( Entry[] entry );

    public void failed ( String error );

    public void error ( Throwable e );
}
