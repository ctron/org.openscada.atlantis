package org.openscada.ae.server.storage.jdbc.internal;

import org.osgi.framework.BundleContext;

public class Activator implements org.osgi.framework.BundleActivator
{
    private static BundleContext bundleContext;

    public static BundleContext getBundleContext ()
    {
        return bundleContext;
    }

    public void start ( BundleContext context ) throws Exception
    {
        bundleContext = context;
    }

    public void stop ( BundleContext context ) throws Exception
    {
        bundleContext = null;
    }
}
