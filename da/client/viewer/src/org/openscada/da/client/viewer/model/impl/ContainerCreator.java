package org.openscada.da.client.viewer.model.impl;

import org.apache.log4j.Logger;
import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.ContainerFactory;

public class ContainerCreator
{
    private static Logger _log = Logger.getLogger ( ContainerCreator.class );
    
    public static ContainerFactory findFactory ( Class clazz ) throws ConfigurationError
    {
        Class factoryClass = getSpecificClass ( clazz );
        
        if ( factoryClass != null )
        {
            _log.debug ( String.format ( "Creating factory using specific factory class %s", factoryClass.getName () ) );
            return instantiateFactory ( factoryClass );
        }
        else
        {
            _log.debug ( String.format ( "Creating default factory" ) );
            return new GenericContainerFactory ( clazz );
        }
    }
    
    private static ContainerFactory instantiateFactory ( Class factoryClass ) throws ConfigurationError
    {
        Object o;
        try
        {
            o = factoryClass.newInstance ();
        }
        catch ( Exception e )
        {
            throw new ConfigurationError ( "Unable to create new instance of factory", e );
        }
        if ( !(o instanceof ContainerFactory) )
        {
            // should never happen since we checked before! But better check twice
            throw new ConfigurationError ( String.format ( "Factory class %s does not implement ContainerFactory", factoryClass.getName () ) );
        }
        return (ContainerFactory)o;
    }

    private static Class getSpecificClass ( Class clazz ) throws ConfigurationError
    {
        // if we have a direct hit return it!
        if ( ContainerFactory.class.isAssignableFrom ( clazz ) )
        {
            return clazz;
        }
        
        try
        {
            Class factoryClazz = Class.forName ( clazz.getName () + "Factory" );
            if ( !ContainerFactory.class.isAssignableFrom ( factoryClazz ) )
            {
                throw new ConfigurationError ( "failed to use class %s as factory for %s since if does not implement ContainerFactory." );
            }
            return factoryClazz;
        }
        catch ( ClassNotFoundException e )
        {
            return null;
        }
    }
}
