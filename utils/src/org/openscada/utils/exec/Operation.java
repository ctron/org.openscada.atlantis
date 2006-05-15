/**
 * This package holds some classes and interfaces used for making synchronous and
 * asynchronous calls to synchronous and asynchronous based operations.
 * 
 * In order execute an operation synchronously use 
 */
package org.openscada.utils.exec;

/**
 * Base interface for operations (either sync or async based).
 * @author jens
 *
 * @param <R> The result type
 * @param <T> The argument type
 */
public interface Operation < R, T >
{
    public R execute ( T arg0 ) throws Exception;
    public OperationResult<R> startExecute ( T arg0 );
    public OperationResult<R> startExecute ( OperationResultHandler<R> handler, T arg0 );
}
