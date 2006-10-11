package org.openscada.da.client.viewer.configurator.xml;

import java.lang.reflect.Constructor;

import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.ObjectFactory;
import org.w3c.dom.Node;

public class XMLObjectFactory implements ObjectFactory, XMLConfigurable
{
    private Class _class = null;
    
    public DynamicObject create ( String id ) throws ConfigurationError
    {
        try
        {
            Constructor ctor = _class.getConstructor ( new Class [] { String.class } );
            if ( ctor == null )
                throw new ConfigurationError ( String.format ( "Unable to instatiate class %s since a constructor with parameter type String is missing" ) );
            
            return (DynamicObject)ctor.newInstance ( new Object[] { id } );
        }
        catch ( Throwable e )
        {
            throw new ConfigurationError ( "Unable to create instance", e );
        }
    }

    @SuppressWarnings("unchecked")
    public void configure ( XMLConfigurationContext context, Node node ) throws ConfigurationError
    {
        Node attr = node.getAttributes ().getNamedItem ( "class" );
        if ( attr != null )
        {
            String className = attr.getNodeValue ();
            try
            {
                _class = Class.forName ( className );
            }
            catch ( ClassNotFoundException e )
            {
                throw new ConfigurationError ( "Unable to create factory", e );
            }
            if ( !DynamicObject.class.isAssignableFrom ( _class ) )
            {
                throw new ConfigurationError ( String.format ( "%s must implement interface DynamicObject", _class.getName () ) );
            }
        }
    }

}
