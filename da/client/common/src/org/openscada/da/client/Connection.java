package org.openscada.da.client;

import org.openscada.core.Variant;
import org.openscada.da.core.browser.Entry;
import org.openscada.utils.exec.OperationResult;
import org.openscada.utils.exec.OperationResultHandler;

public interface Connection extends org.openscada.core.client.Connection
{
    public Entry[] browse ( String [] path ) throws Exception;
    public OperationResult<Entry[]> startBrowse ( String [] path, Variant value );
    public OperationResult<Entry[]> startBrowse ( String [] path, OperationResultHandler<Entry[]> handler );
}
