package org.openscada.da.core.common.impl;

import org.openscada.utils.exec.SyncBasedOperation;

public class WriteOperation extends SyncBasedOperation<Object,WriteOperationArguments>
{
    public Object execute ( WriteOperationArguments arg0 ) throws Exception
    {
        arg0.item.setValue ( arg0.value );
       return null;
    }
    
}
