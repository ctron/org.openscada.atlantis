package org.openscada.ae.client;

import java.util.Set;

import org.openscada.ae.core.QueryDescription;
import org.openscada.core.OperationException;
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningOperation;

public interface Connection
{
    public LongRunningOperation startList ( LongRunningListener listener );    
    public Set<QueryDescription> completeList ( LongRunningOperation operation ) throws OperationException;
    public Set<QueryDescription> list () throws InterruptedException, OperationException;
    
    public void subscribe ( String queryId, org.openscada.ae.core.Listener listener, int maxBatchSize, int archiveSet );
    public void unsubscribe ( String queryId, org.openscada.ae.core.Listener listener );
}
