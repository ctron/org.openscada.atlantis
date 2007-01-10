/**
 * 
 */
package org.openscada.utils.exec;


public interface LongRunningListener
{
    void stateChanged ( LongRunningState state, Throwable error );
}