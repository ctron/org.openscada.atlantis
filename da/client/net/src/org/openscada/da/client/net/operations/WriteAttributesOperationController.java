package org.openscada.da.client.net.operations;

import java.util.Map;

import org.openscada.da.core.Variant;
import org.openscada.net.base.ConnectionHandlerBase;
import org.openscada.net.base.LongRunningController;
import org.openscada.net.base.LongRunningOperation;
import org.openscada.net.base.MessageProcessor;
import org.openscada.net.base.LongRunningController.Listener;
import org.openscada.net.base.data.Message;
import org.openscada.net.da.handler.Messages;
import org.openscada.net.da.handler.WriteAttributesOperation;

public class WriteAttributesOperationController
{
    private LongRunningController _controller = null;
    
    public WriteAttributesOperationController ( ConnectionHandlerBase connection )
    {
        _controller = new LongRunningController ( connection, Messages.CC_CANCEL_OPERATION, Messages.CC_WRITE_ATTRIBUTES_OPERATION_RESULT );
    }

    public void register ( MessageProcessor processor )
    {
        _controller.register ( processor );
    }

    public void unregister ( MessageProcessor processor )
    {
        _controller.unregister ( processor );
    }
    
    public LongRunningOperation start ( String itemName, Map<String,Variant> attributes, Listener listener )
    {
        Message message = WriteAttributesOperation.createRequest ( itemName, attributes );
        
        return _controller.start ( message, listener );
    }
    
}
