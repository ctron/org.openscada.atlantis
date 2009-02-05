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

import java.lang.reflect.Constructor;

import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.ObjectFactory;

public class GenericObjectFactory implements ObjectFactory
{
    private Class<?> _class = null;

    public GenericObjectFactory ()
    {
    }

    public GenericObjectFactory ( final Class<?> clazz )
    {
        this._class = clazz;
    }

    public DynamicObject create ( final String id ) throws ConfigurationError
    {
        if ( this._class == null )
        {
            throw new ConfigurationError ( "Not class set for construction" );
        }
        try
        {
            final Constructor<?> ctor = this._class.getConstructor ( new Class[] { String.class } );
            if ( ctor == null )
            {
                throw new ConfigurationError ( String.format ( "Unable to instatiate class %s since a constructor with parameter type String is missing" ) );
            }

            return (DynamicObject)ctor.newInstance ( new Object[] { id } );
        }
        catch ( final Throwable e )
        {
            throw new ConfigurationError ( String.format ( "Unable to create instance of %s", this._class.getName () ), e );
        }
    }

    public void setClass ( final Class<?> clazz )
    {
        this._class = clazz;
    }
}
