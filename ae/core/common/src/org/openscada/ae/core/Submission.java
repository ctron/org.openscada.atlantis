package org.openscada.ae.core;

import java.util.Properties;

/**
 * Interface to submit an event to a storage module
 * @author Jens Reimann <jens.reimann@inavare.net>
 *
 */
public interface Submission
{

    /**
     * Submit an event to the storage module that implements this interface
     * 
     * Depending on the implementation of the interface the event is processed
     * and possibly stored. If the event is stored all subscribers must be
     * notifed about the new event if appropriate.
     * 
     * In order to maintain a session less interface the caller must provided all
     * connection properties with each call.
     * 
     * If the call returns without error the event must be stored successfully
     * by the implementation.
     * 
     * @param properties the connection properties
     * @param event the event to store
     * @throws Throwable any case of error
     */
    public void submitEvent ( Properties properties, Event event ) throws Throwable;

}
