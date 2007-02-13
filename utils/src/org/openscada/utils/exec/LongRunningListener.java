/**
 * 
 */
package org.openscada.utils.exec;


public interface LongRunningListener
{
    void stateChanged ( LongRunningOperation operation, LongRunningState state, Throwable error );
}