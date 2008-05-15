package org.openscada.da.server.common;

import org.openscada.da.core.server.Hive;

/**
 * A service that is offered internally by the {@link Hive}
 * <p>
 * The service is only valid after {@link #init()} has been called by the hive
 * and up to {@link #dispose()} is called by the hive
 * @author Jens Reimann
 *
 */
public interface HiveService
{
    /**
     * The hive asks the service to initialize
     */
    public abstract void init ();
    
    /**
     * The hive asks the service to dispose and free all resources 
     */
    public abstract void dispose ();
}
