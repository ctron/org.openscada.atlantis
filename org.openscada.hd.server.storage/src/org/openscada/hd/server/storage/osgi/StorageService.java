package org.openscada.hd.server.storage.osgi;

import java.util.Map;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationListener;
import org.openscada.ca.ConfigurationState;
import org.openscada.ca.SelfManagedConfigurationFactory;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.osgi.framework.BundleContext;

/**
 * Storage service that manages available storage historical item services.
 * @author Ludwig Straub
 */
public class StorageService implements SelfManagedConfigurationFactory
{
    /** OSGi bundle context. */
    private final BundleContext bundleContext;

    /**
     * Constructor.
     * @param bundleContext OSGi bundle context
     */
    public StorageService ( BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    /**
     * @see org.openscada.ca.SelfManagedConfigurationFactory#addConfigurationListener
     */
    public void addConfigurationListener ( ConfigurationListener listener )
    {
    }

    /**
     * @see org.openscada.ca.SelfManagedConfigurationFactory#removeConfigurationListener
     */
    public void removeConfigurationListener ( ConfigurationListener listener )
    {
    }

    /**
     * @see org.openscada.ca.SelfManagedConfigurationFactory#update
     */
    public NotifyFuture<Configuration> update ( final String configurationId, final Map<String, String> properties )
    {
        return new InstantFuture<Configuration> ( new Configuration () {
            public ConfigurationState getState ()
            {
                return ConfigurationState.APPLIED;
            }

            public String getId ()
            {
                return configurationId;
            }

            public String getFactoryId ()
            {
                return "org.tot";
            }

            public Throwable getErrorInformation ()
            {
                return null;
            }

            public Map<String, String> getData ()
            {
                return properties;
            }
        } );
    }

    /**
     * @see org.openscada.ca.SelfManagedConfigurationFactory#delete
     */
    public NotifyFuture<Configuration> delete ( String configurationId )
    {
        return null;
    }
}
