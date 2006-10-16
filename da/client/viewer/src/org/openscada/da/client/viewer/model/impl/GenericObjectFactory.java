package org.openscada.da.client.viewer.model.impl;

import java.lang.reflect.Constructor;

import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.ObjectFactory;

public class GenericObjectFactory implements ObjectFactory
{
    private Class _class = null;

    public GenericObjectFactory ()
    {
    }

    public GenericObjectFactory ( Class clazz )
    {
        _class = clazz;
    }

    public DynamicObject create ( String id ) throws ConfigurationError
    {
        if ( _class == null )
            throw new ConfigurationError ( "Not class set for construction" );
        try
        {
            Constructor ctor = _class.getConstructor ( new Class[] { String.class } );
            if ( ctor == null )
                throw new ConfigurationError (
                        String.format ( "Unable to instatiate class %s since a constructor with parameter type String is missing" ) );

            return (DynamicObject)ctor.newInstance ( new Object[] { id } );
        }
        catch ( Throwable e )
        {
            throw new ConfigurationError ( String.format ( "Unable to create instance of %s", _class.getName () ), e );
        }
    }

    public void setClass ( Class clazz )
    {
        _class = clazz;
    }
}
