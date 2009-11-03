package org.openscada.hd.server.storage.internal;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationState;

/**
 * This is the internally used implementation of the CA configuration interface.
 * @author Ludwig Straub
 */
public class ConfigurationImpl extends org.openscada.hsdb.configuration.Configuration implements Configuration
{
    /** Id of the factory that is used to process the configuration and create the related objects. */
    private String factoryId;

    /** State of the configuration. */
    private ConfigurationState state;

    /** Error information. */
    private Throwable errorInformation;

    /**
     * Standard constructor.
     */
    public ConfigurationImpl ()
    {
    }

    /**
     * Copy constructor.
     * @param configuration configuration object from which data has to be copied
     */
    public ConfigurationImpl ( final org.openscada.hsdb.configuration.Configuration configuration )
    {
        super ( configuration );
        if ( configuration instanceof ConfigurationImpl )
        {
            final ConfigurationImpl configurationImpl = (ConfigurationImpl)configuration;
            this.errorInformation = configurationImpl.getErrorInformation ();
            this.factoryId = configurationImpl.getFactoryId ();
            this.state = configurationImpl.getState ();
        }
    }

    /**
     * This method returns the id of the factory that is used to process the configuration and create the related objects.
     * @return id of the factory that is used to process the configuration and create the related objects
     */
    public String getFactoryId ()
    {
        return factoryId;
    }

    /**
     * This method sets the id of the factory that is used to process the configuration and create the related objects.
     * @param factoryId id of the factory that is used to process the configuration and create the related objects
     */
    public void setFactoryId ( final String factoryId )
    {
        this.factoryId = factoryId;
    }

    /**
     * This method returns the state of the configuration.
     * @return state of the configuration
     */
    public ConfigurationState getState ()
    {
        return state;
    }

    /**
     * This method sets the state of the configuration.
     * @param state state of the configuration
     */
    public void setState ( final ConfigurationState state )
    {
        this.state = state;
    }

    /**
     * This method returns the error information.
     * @return error information
     */
    public Throwable getErrorInformation ()
    {
        return errorInformation;
    }

    /**
     * This method sets the error information.
     * @param errorInformation error information
     */
    public void setErrorInformation ( final Throwable errorInformation )
    {
        this.errorInformation = errorInformation;
    }
}
