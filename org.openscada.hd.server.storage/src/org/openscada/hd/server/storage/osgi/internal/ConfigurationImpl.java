package org.openscada.hd.server.storage.osgi.internal;

import java.util.Map;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationState;

public class ConfigurationImpl implements Configuration
{
    private String factoryId;

    public ConfigurationImpl ()
    {

    }

    public ConfigurationImpl ( Configuration configuration )
    {

    }

    public String getFactoryId ()
    {
        return factoryId;
    }

    public void setFactoryId ( String factoryId )
    {
        this.factoryId = factoryId;
    }

    public String getId ()
    {
        return id;
    }

    public void setId ( String id )
    {
        this.id = id;
    }

    public ConfigurationState getState ()
    {
        return state;
    }

    public void setState ( ConfigurationState state )
    {
        this.state = state;
    }

    public Throwable getErrorInformation ()
    {
        return errorInformation;
    }

    public void setErrorInformation ( Throwable errorInformation )
    {
        this.errorInformation = errorInformation;
    }

    public Map<String, String> getData ()
    {
        return data;
    }

    public void setData ( Map<String, String> data )
    {
        this.data = data;
    }

    private String id;

    private ConfigurationState state;

    private Throwable errorInformation;

    private Map<String, String> data;
}
