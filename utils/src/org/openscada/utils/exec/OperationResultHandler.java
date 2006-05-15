package org.openscada.utils.exec;

/**
 * Interface for handling result notify asynchronously
 * @author jens
 *
 * @param <R> The result type
 */
public interface OperationResultHandler < R >
{
    /**
     * Gets called in the case an error occurred.
     * @param e The exception that was thrown
     */
    public void failure ( Exception e );
    
    /**
     * Gets called when the operation succeeded
     * @param result
     */
    public void success ( R result );
}
