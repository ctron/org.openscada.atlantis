/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

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
