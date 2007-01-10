package org.openscada.da.client;

import java.util.Map;

import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.browser.Entry;
import org.openscada.utils.exec.LongRunningOperation;
import org.openscada.utils.exec.OperationResult;
import org.openscada.utils.exec.OperationResultHandler;
import org.openscada.utils.jobqueue.OperationManager.Listener;

public interface Connection extends org.openscada.core.client.Connection
{
    public Entry[] browse ( String [] path ) throws Exception;
    public OperationResult<Entry[]> startBrowse ( String [] path, Variant value );
    public OperationResult<Entry[]> startBrowse ( String [] path, OperationResultHandler<Entry[]> handler );
    
    public void write ( String itemName, Variant value ) throws InterruptedException, OperationException;
    
    public void write ( String itemName, Variant value, Listener listener ) throws InterruptedException, OperationException;
    
    public LongRunningOperation startWrite ( String itemName, Variant value, Listener listener );
    
    public void completeWrite ( LongRunningOperation op ) throws OperationException;
    
    public void writeAttributes ( String itemId, Map<String,Variant> attributes ) throws InterruptedException, OperationException;
    
    public void writeAttributes ( String itemId, Map<String,Variant> attributes, Listener listener ) throws InterruptedException, OperationException;
    
    public LongRunningOperation startWriteAttributes ( String itemId, Map<String,Variant> attributes, Listener listener );
    
    public WriteAttributeResults completeWriteAttributes ( LongRunningOperation operation ) throws OperationException;
}
