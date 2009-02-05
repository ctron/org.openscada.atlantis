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

package org.openscada.da.client.viewer.model.impl;

import org.apache.log4j.Logger;
import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.ObjectFactory;

public class DynamicObjectCreator
{
    private static Logger _log = Logger.getLogger ( DynamicObjectCreator.class );

    public static ObjectFactory findFactory ( final Class<?> clazz ) throws ConfigurationError
    {
        final Class<?> factoryClass = getSpecificClass ( clazz );

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

    private static ObjectFactory instantiateFactory ( final Class<?> factoryClass ) throws ConfigurationError
    {
        Object o;
        try
        {
            o = factoryClass.newInstance ();
        }
        catch ( final Exception e )
        {
            throw new ConfigurationError ( "Unable to create new instance of factory", e );
        }
        if ( ! ( o instanceof ObjectFactory ) )
        {
            // should never happen since we checked before! But better check twice
            throw new ConfigurationError ( String.format ( "Factory class %s does not implement ObjectFactory", factoryClass.getName () ) );
        }
        return (ObjectFactory)o;
    }

    private static Class<?> getSpecificClass ( final Class<?> clazz ) throws ConfigurationError
    {
        // if we have a direct hit return it!
        if ( ObjectFactory.class.isAssignableFrom ( clazz ) )
        {
            return clazz;
        }

        try
        {
            final Class<?> factoryClazz = Class.forName ( clazz.getName () + "Factory" );
            if ( !ObjectFactory.class.isAssignableFrom ( factoryClazz ) )
            {
                throw new ConfigurationError ( "failed to use class %s as factory for %s since if does not implement ObjectFactory." );
            }
            return factoryClazz;
        }
        catch ( final ClassNotFoundException e )
        {
            return null;
        }
    }
}
