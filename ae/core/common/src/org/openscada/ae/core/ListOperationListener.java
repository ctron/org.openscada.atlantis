package org.openscada.ae.core;


public interface ListOperationListener
{
    public abstract void complete ( QueryDescription[] queries );
    public abstract void failed ( Throwable error ); 
}
