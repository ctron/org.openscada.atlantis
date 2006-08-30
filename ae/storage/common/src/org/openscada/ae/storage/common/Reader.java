package org.openscada.ae.storage.common;

import org.openscada.ae.core.Event;

public interface Reader extends ReaderBase
{
    boolean hasMoreElements ();
    Event[] fetchNext ( int maxBatchSize );
    
}
