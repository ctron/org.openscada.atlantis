package org.openscada.da.client;

import java.util.Map;

import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.da.core.Location;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.browser.Entry;
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningOperation;
import org.openscada.utils.exec.OperationResult;
import org.openscada.utils.exec.OperationResultHandler;

public interface Connection extends org.openscada.core.client.Connection
{
    public abstract Entry[] browse ( String [] path ) throws Exception;
    public abstract OperationResult<Entry[]> startBrowse ( String [] path, Variant value );
    public abstract OperationResult<Entry[]> startBrowse ( String [] path, OperationResultHandler<Entry[]> handler );
    
    public abstract void write ( String itemName, Variant value ) throws InterruptedException, OperationException;
    public abstract void write ( String itemName, Variant value, LongRunningListener listener ) throws InterruptedException, OperationException;
    public abstract LongRunningOperation startWrite ( String itemName, Variant value, LongRunningListener listener );
    public abstract void completeWrite ( LongRunningOperation op ) throws OperationException;
    
    public abstract void writeAttributes ( String itemId, Map<String,Variant> attributes ) throws InterruptedException, OperationException;
    public abstract void writeAttributes ( String itemId, Map<String,Variant> attributes, LongRunningListener listener ) throws InterruptedException, OperationException;
    public abstract LongRunningOperation startWriteAttributes ( String itemId, Map<String,Variant> attributes, LongRunningListener listener );
    public abstract WriteAttributeResults completeWriteAttributes ( LongRunningOperation operation ) throws OperationException;
    
    public abstract void addFolderListener ( FolderListener listener, Location location );
    public abstract void addFolderWatcher ( FolderWatcher watcher );
    public abstract void removeFolderListener ( FolderListener listener, Location location );
    public abstract void removeFolderWatcher ( FolderWatcher watcher );
    
    public abstract void subscribeItem ( String itemId, boolean initial );
    public abstract void unsubscribeItem ( String itemId );
    public abstract ItemUpdateListener setItemUpdateListener ( String itemId, ItemUpdateListener listener );
}
