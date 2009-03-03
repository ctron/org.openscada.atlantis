package org.openscada.da.client;

import org.openscada.da.core.WriteAttributeResults;

public interface WriteAttributeOperationCallback
{
    public abstract void complete ( WriteAttributeResults result );

    public abstract void failed ( String error );

    public abstract void error ( Throwable e );
}
