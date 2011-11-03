package org.openscada.ae.server.storage.memory;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ae.server.storage.Storage;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

    private MemoryStorage memoryStorage;

    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        this.memoryStorage = new MemoryStorage ();

        final Dictionary<String, Object> props = new Hashtable<String, Object> ();
        context.registerService ( new String[] { MemoryStorage.class.getName (), Storage.class.getName () }, this.memoryStorage, props );
    }

    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        this.memoryStorage = null;
        return;
    }
}
