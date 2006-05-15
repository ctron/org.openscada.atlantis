package org.openscada.utils.exec;

public interface Operation < R, T >
{
    public R execute ( T arg0 ) throws Exception;
    public OperationResult<R> startExecute ( T arg0 );
    public OperationResult<R> startExecute ( OperationResultHandler<R> handler, T arg0 );
}
