/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.utils.factory;

/**
 * The bean helper helps you to create beans ;-)
 * @author Jens Reimann
 *
 */
public class BeanHelper
{
    public static Object create ( BeanFactory factory, String reference ) throws BeanCreationException
    {
        try
        {
            return factory.create ( reference );
        }
        catch ( Exception e )
        {
            throw new BeanCreationException ( e );
        }
    }

    public static Object create ( Class<? extends BeanFactory> factoryClass, String reference ) throws BeanCreationException
    {
        try
        {
            return factoryClass.newInstance ().create ( reference );
        }
        catch ( Exception e )
        {
            throw new BeanCreationException ( e );
        }
    }

    @SuppressWarnings ( "unchecked" )
    public static Object create ( String factoryClassName, String reference ) throws BeanCreationException
    {
        try
        {
            return create ( (Class<BeanFactory>)Class.forName ( factoryClassName ), reference );
        }
        catch ( ClassNotFoundException e )
        {
            throw new BeanCreationException ( e );
        }
    }

    /**
     * Create a new bean based with a required class
     * @param factoryClassName
     * @param reference
     * @param requiredClass the class that is required
     * @return the new instance which can be casted to requiredClass
     * @throws BeanCreationException in the case anything goes wrong or the bean created was not the required type
     */
    public static Object create ( String factoryClassName, String reference, Class<?> requiredClass ) throws BeanCreationException
    {
        Object o = create ( factoryClassName, reference );
        try
        {
            return requiredClass.cast ( o );
        }
        catch ( ClassCastException e )
        {
            throw new BeanCreationException ( e );
        }
    }
}
