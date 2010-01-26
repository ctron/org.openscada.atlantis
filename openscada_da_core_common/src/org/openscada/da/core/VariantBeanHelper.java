/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.core;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.OperationException;
import org.openscada.core.Variant;

public class VariantBeanHelper
{
    /**
     * Extract the property data as string/variant map
     * @param source the source object
     * @return the map with bean data
     * @throws IntrospectionException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Map<String, Variant> extract ( final Object source ) throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        final Map<String, Variant> result = new HashMap<String, Variant> ();

        final BeanInfo bi = Introspector.getBeanInfo ( source.getClass () );

        for ( final PropertyDescriptor pd : bi.getPropertyDescriptors () )
        {
            final Method m = pd.getReadMethod ();
            if ( m != null )
            {
                result.put ( pd.getName (), new Variant ( m.invoke ( source ) ) );
            }
        }
        return result;
    }

    public static void apply ( final Map<String, Variant> data, final Object target, final WriteAttributeResults results ) throws IntrospectionException
    {
        final BeanInfo bi = Introspector.getBeanInfo ( target.getClass () );

        for ( final Map.Entry<String, Variant> entry : data.entrySet () )
        {
            final PropertyDescriptor pd = findDescriptor ( bi, entry.getKey () );
            if ( pd != null )
            {
                try
                {
                    applyValue ( target, pd, entry.getValue () );
                    results.put ( entry.getKey (), WriteAttributeResult.OK );
                }
                catch ( final Exception e )
                {
                    results.put ( entry.getKey (), new WriteAttributeResult ( e ) );
                }
            }
            else
            {
                results.put ( entry.getKey (), new WriteAttributeResult ( new IllegalArgumentException ( String.format ( "'%s' is not a property name of '%s'", entry.getKey (), target.getClass ().getName () ) ) ) );
            }
        }
    }

    public static void applyValue ( final Object target, final PropertyDescriptor pd, final Variant value ) throws OperationException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        final Method m = pd.getWriteMethod ();
        if ( m == null )
        {
            throw new OperationException ( String.format ( "Property '%s' is write protected", pd.getName () ) );
        }

        final Class<?> targetType = pd.getPropertyType ();
        if ( targetType.isAssignableFrom ( Variant.class ) )
        {
            // direct set using variant type
            m.invoke ( target, value );
        }
    }

    private static PropertyDescriptor findDescriptor ( final BeanInfo bi, final String propertyName )
    {
        for ( final PropertyDescriptor pd : bi.getPropertyDescriptors () )
        {
            if ( pd.getName ().equals ( propertyName ) )
            {
                return pd;
            }
        }
        return null;
    }
}
