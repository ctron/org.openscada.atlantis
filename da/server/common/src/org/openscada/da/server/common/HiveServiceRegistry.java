package org.openscada.da.server.common;


public interface HiveServiceRegistry
{

    /**
     * Register a new service for a service name.
     * <p>
     * If service was previously registered with this name, the new service will
     * take its place and the old service will get disposed
     * @param serviceName the name of the new service
     * @param service the new service
     * @return the old service that got disposed or <code>null</code> if no service got disposed
     */
    public abstract HiveService registerService ( String serviceName, HiveService service );

    /**
     * Unregister a names service
     * @param serviceName the service name to unregister
     * @return the HiveService that was removed or <code>null</code> if none with this name existed
     */
    public abstract HiveService unregisterService ( String serviceName );

    /**
     * Get a registered service from the registry
     * @param serviceName the server to fetch
     * @return the service instance of <code>null</code> if no service with that name was registered
     */
    public abstract HiveService getService ( String serviceName );
}