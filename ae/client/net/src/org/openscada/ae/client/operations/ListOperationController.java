package org.openscada.ae.client.operations;

import org.openscada.ae.net.ListMessage;
import org.openscada.ae.net.Messages;
import org.openscada.net.base.ConnectionHandlerBase;
import org.openscada.net.base.LongRunningController;
import org.openscada.net.base.LongRunningOperation;
import org.openscada.net.base.MessageProcessor;
import org.openscada.net.base.LongRunningController.Listener;

public class ListOperationController
{
    private LongRunningController _controller = null;
    
    public ListOperationController ( ConnectionHandlerBase connection )
    {
        _controller = new LongRunningController ( connection, Messages.CC_CANCEL_OPERATION, Messages.CC_LIST_REPLY );
    }

    public void register ( MessageProcessor processor )
    {
        _controller.register ( processor );
    }

    public void unregister ( MessageProcessor processor )
    {
        _controller.unregister ( processor );
    }
    
    public LongRunningOperation start ( Listener listener )
    {
        ListMessage message = new ListMessage ();
        return _controller.start ( message.toMessage (), listener );
    }
    
}
