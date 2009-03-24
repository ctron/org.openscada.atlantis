package org.openscada.ae.client;

public interface OperationListener<T>
{
    public void handleSuccess ( final T result );

    public void handleError ( Throwable error );
}
