package org.openscada.da.client.net.operations;

import org.openscada.net.base.ConnectionHandlerBase;
import org.openscada.net.base.LongRunningController;
import org.openscada.net.base.MessageProcessor;
import org.openscada.net.base.data.Message;
import org.openscada.net.da.handler.ListBrowser;
import org.openscada.net.da.handler.Messages;
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningOperation;

public class BrowseOperationController
{
    private LongRunningController _controller = null;
    
    public BrowseOperationController ( ConnectionHandlerBase connection )
    {
        _controller = new LongRunningController ( connection, Messages.CC_CANCEL_OPERATION, Messages.CC_BROWSER_LIST_RES );
    }

    public void register ( MessageProcessor processor )
    {
        _controller.register ( processor );
    }

    public void unregister ( MessageProcessor processor )
    {
        _controller.unregister ( processor );
    }
    
    public LongRunningOperation start ( String [] location, LongRunningListener listener )
    {
        Message message = ListBrowser.createRequest ( location );
        
        return _controller.start ( message, listener );
    }
    
}
