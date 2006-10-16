package org.openscada.da.client.viewer.configurator.xml;

import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.impl.GenericObjectFactory;
import org.w3c.dom.Node;

public class XMLObjectFactory extends GenericObjectFactory implements XMLConfigurable
{
    @SuppressWarnings("unchecked")
    public void configure ( XMLConfigurationContext context, Node node ) throws ConfigurationError
    {
        Node attr = node.getAttributes ().getNamedItem ( "class" );
        if ( attr != null )
        {
            String className = attr.getNodeValue ();
            try
            {   
                Class clazz = Class.forName ( className );
                if ( !DynamicObject.class.isAssignableFrom ( clazz ) )
                {
                    throw new ConfigurationError ( String.format ( "%s must implement interface DynamicObject", clazz.getName () ) );
                }
                setClass ( clazz );
            }
            catch ( ClassNotFoundException e )
            {
                throw new ConfigurationError ( "Unable to create factory", e );
            }
        }
    }

}
