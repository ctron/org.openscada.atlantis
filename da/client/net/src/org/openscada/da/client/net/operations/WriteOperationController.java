package org.openscada.da.client.net.operations;

import org.openscada.da.core.data.Variant;
import org.openscada.net.base.ConnectionHandlerBase;
import org.openscada.net.base.LongRunningController;
import org.openscada.net.base.LongRunningOperation;
import org.openscada.net.base.MessageProcessor;
import org.openscada.net.base.LongRunningController.Listener;
import org.openscada.net.base.data.Message;
import org.openscada.net.da.handler.Messages;

public class WriteOperationController
{
    private LongRunningController _controller = null;
    
    public WriteOperationController ( ConnectionHandlerBase connection )
    {
        _controller = new LongRunningController ( connection, 0, Messages.CC_WRITE_OPERATION_RESULT );
    }

    public void register ( MessageProcessor processor )
    {
        _controller.register ( processor );
    }

    public void unregister ( MessageProcessor processor )
    {
        _controller.unregister ( processor );
    }
    
    public LongRunningOperation start ( String itemName, Variant value, Listener listener )
    {
        Message message = org.openscada.net.da.handler.WriteOperation.create ( itemName, value );
        
        return _controller.start ( message, listener );
    }
    
}
