package org.openscada.da.client.viewer.configurator.xml;

import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.ObjectFactory;
import org.w3c.dom.Node;

public class XMLObjectFactory implements ObjectFactory, XMLConfigurable
{
    private Class _class = null;
    
    public DynamicObject create () throws ConfigurationError
    {
        try
        {
            return (DynamicObject)_class.newInstance ();
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
