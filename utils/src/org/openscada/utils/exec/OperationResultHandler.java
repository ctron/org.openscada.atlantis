package org.openscada.utils.exec;

public interface OperationResultHandler < R >
{
    public void failure ( Exception e );
    public void success ( R result );
}
