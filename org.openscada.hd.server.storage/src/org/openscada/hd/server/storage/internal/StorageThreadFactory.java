package org.openscada.hd.server.storage.internal;

import java.util.concurrent.ThreadFactory;

/**
 * This class provides a thread factory which creates named threads.
 * @author Ludwig Straub
 */
public class StorageThreadFactory
{
    /**
     * This method creates a thread factory which creates threads with the specified name.
     * @param name name of the new threads that have to be created
     * @return new created thread factory
     */
    public static ThreadFactory createFactory ( final String name )
    {
        return new ThreadFactory () {
            public Thread newThread ( final Runnable arg0 )
            {
                return new Thread ( arg0, name );
            }
        };
    }
}
