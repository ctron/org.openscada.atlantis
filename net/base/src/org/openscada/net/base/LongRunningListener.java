/**
 * 
 */
package org.openscada.net.base;

import org.openscada.net.base.data.Message;

public interface LongRunningListener
{
    void stateChanged ( LongRunningState state, Message reply, Throwable error );
}