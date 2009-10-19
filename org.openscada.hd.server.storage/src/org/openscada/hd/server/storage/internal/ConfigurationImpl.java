package org.openscada.hd.server.storage.internal;

import java.util.Collections;
import java.util.Map;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationState;

/**
 * This is the internally used implementation of the CA configuration interface.
 * @author Ludwig Straub
 */
public class ConfigurationImpl implements Configuration
{
    /** Id of the factory that is used to process the configuration and create the related objects. */
    private String factoryId;

    /** Id of the configuration itself. */
    private String id;

    /** State of the configuration. */
    private ConfigurationState state;

    /** Error information. */
    private Throwable errorInformation;

    /** Additional configuration data. */
    private Map<String, String> data;

    /**
     * Standard constructor.
     */
    public ConfigurationImpl ()
    {
    }

    /**
     * Copy constructor.
     * @param configuration configuration object from whcih data has to be copied
     */
    public ConfigurationImpl ( final Configuration configuration )
    {
        if ( configuration != null )
        {
            this.errorInformation = configuration.getErrorInformation ();
            this.factoryId = configuration.getFactoryId ();
            this.id = configuration.getId ();
            this.state = configuration.getState ();
            setData ( configuration.getData () );
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
     * This method returns the id of the configuration itself.
     * @return id of the configuration itself
     */
    public String getId ()
    {
        return id;
    }

    /**
     * This method sets the id of the configuration itself.
     * @param id id of the configuration itself
     */
    public void setId ( final String id )
    {
        this.id = id;
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

    /**
     * This method returns the additional configuration data.
     * @return additional configuration data
     */
    public Map<String, String> getData ()
    {
        return data == null ? null : Collections.unmodifiableMap ( data );
    }

    /**
     * This method sets the additional configuration data.
     * @param data additional configuration data
     */
    public void setData ( final Map<String, String> data )
    {
        this.data = data == null ? null : Collections.unmodifiableMap ( data );
    }
}
