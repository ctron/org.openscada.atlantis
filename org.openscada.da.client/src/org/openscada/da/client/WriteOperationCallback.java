package org.openscada.da.client;

public interface WriteOperationCallback
{
    public abstract void complete ();

    public abstract void failed ( String error );

    public abstract void error ( Throwable e );
}
