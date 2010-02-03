package org.openscada.hd.exporter.http.random;

import java.util.Properties;

import org.openscada.hd.exporter.http.HttpExporter;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class Activator implements BundleActivator
{

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        Properties props = new Properties ();
        props.put ( Constants.SERVICE_RANKING, 100 );
        context.registerService ( HttpExporter.class.getName (), new RandomHttpExporter (), props );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
    }
}
