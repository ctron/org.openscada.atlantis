package org.openscada.da.client.viewer.model.impl;

import org.apache.log4j.Logger;
import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.ObjectFactory;

public class DynamicObjectCreator
{
    private static Logger _log = Logger.getLogger ( DynamicObjectCreator.class );
    
    public static ObjectFactory findFactory ( Class clazz ) throws ConfigurationError
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
            return new GenericObjectFactory ( clazz );
        }
    }
    
    private static ObjectFactory instantiateFactory ( Class factoryClass ) throws ConfigurationError
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
        if ( !(o instanceof ObjectFactory) )
        {
            // should never happen since we checked before! But better check twice
            throw new ConfigurationError ( String.format ( "Factory class %s does not implement ObjectFactory", factoryClass.getName () ) );
        }
        return (ObjectFactory)o;
    }

    private static Class getSpecificClass ( Class clazz ) throws ConfigurationError
    {
        // if we have a direct hit return it!
        if ( ObjectFactory.class.isAssignableFrom ( clazz ) )
        {
            return clazz;
        }
        
        try
        {
            Class factoryClazz = Class.forName ( clazz.getName () + "Factory" );
            if ( !ObjectFactory.class.isAssignableFrom ( factoryClazz ) )
            {
                throw new ConfigurationError ( "failed to use class %s as factory for %s since if does not implement ObjectFactory. Falling back to default implementation!" );
            }
            return factoryClazz;
        }
        catch ( ClassNotFoundException e )
        {
            return null;
        }
    }
}
