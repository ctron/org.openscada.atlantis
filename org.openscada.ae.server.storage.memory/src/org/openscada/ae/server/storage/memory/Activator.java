package org.openscada.ae.server.storage.memory;

import java.util.Properties;

import org.openscada.ae.server.storage.Storage;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

    private MemoryStorage memoryStorage;

    public void start ( final BundleContext context ) throws Exception
    {
        this.memoryStorage = new MemoryStorage ();
        Properties props = new Properties ();
        context.registerService ( new String[] { MemoryStorage.class.getName (), Storage.class.getName () }, this.memoryStorage, props );
    }

    public void stop ( final BundleContext context ) throws Exception
    {
        this.memoryStorage = null;
        return;
    }
}
