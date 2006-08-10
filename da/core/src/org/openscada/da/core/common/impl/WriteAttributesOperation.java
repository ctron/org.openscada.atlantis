package org.openscada.da.core.common.impl;

import java.util.Map;

import org.openscada.da.core.WriteAttributesOperationListener;
import org.openscada.da.core.WriteAttributesOperationListener.Results;
import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.data.Variant;
import org.openscada.utils.jobqueue.RunnableCancelOperation;

public class WriteAttributesOperation extends RunnableCancelOperation
{

    private DataItem _item = null;
    private WriteAttributesOperationListener _listener = null;
    private Map<String, Variant> _attributes = null;
    
    public WriteAttributesOperation ( DataItem item, WriteAttributesOperationListener listener, Map<String, Variant> attributes )
    {
        _item = item;
        _listener = listener;
        _attributes = attributes;
    }
    
    public void run ()
    {
        Results result = _item.setAttributes ( _attributes );
        
        if ( !isCanceled () )
        {
            _listener.complete ( result );
        }
    }

}
