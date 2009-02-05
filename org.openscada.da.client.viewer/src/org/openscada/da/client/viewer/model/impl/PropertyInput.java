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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.client.viewer.model.AlreadyConnectedException;
import org.openscada.da.client.viewer.model.Connector;
import org.openscada.da.client.viewer.model.InputDefinition;
import org.openscada.da.client.viewer.model.NotConnectedException;
import org.openscada.da.client.viewer.model.Type;

public class PropertyInput implements InputDefinition
{
    private static Logger _log = Logger.getLogger ( PropertyInput.class );
    
    private Connector _connector = null;
    
    private Object _object = null;
    private String _name = null;
    
    public PropertyInput ( Object object, String name )
    {
        super ();
        _object = object;
        _name = name;
    }
    
    public String getName ()
    {
        return _name;
    }

    protected PropertyDescriptor getPropertyDescriptor () throws IntrospectionException
    {
        BeanInfo bi = Introspector.getBeanInfo ( _object.getClass () );
        for ( PropertyDescriptor pd : bi.getPropertyDescriptors () )
        {
            if ( pd.getName ().equals ( _name ) )
                return pd;
        }
        return null;
    }
    
    public EnumSet<Type> getSupportedTypes ()
    {
        try
        {
            PropertyDescriptor pd = getPropertyDescriptor ();
            
            return Helper.classToType ( pd.getPropertyType () );
        }
        catch ( IntrospectionException e )
        {
            _log.warn ( "Failed to get supported types", e );
            return EnumSet.noneOf ( Type.class );
        }
    }

    public void update ( Type type, Object value )
    {
       if ( !getSupportedTypes ().contains ( type ) )
       {
           _log.info ( String.format ( "Type not supported: %s %s", type, value ) );
           return;
       }
       try
       {
           setValue ( type, value );
       }
       catch ( Exception e )
       {
           _log.warn ( String.format ( "Failed to set value of property '%s'", _name ), e );
       }
    }
    
    protected void setValue ( Type type, Object value ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IntrospectionException
    {
        PropertyDescriptor pd = getPropertyDescriptor ();
        Method method = pd.getWriteMethod ();
        
        if ( method == null )
            return;
        
        if ( pd.getPropertyType ().equals ( Object.class  ) )
        {
            // simply pass through
            method.invoke ( _object, new Object [] { value } );
        }
        else if ( pd.getPropertyType ().isAssignableFrom ( Variant.class ) )
        {
            // we have a variant type so try to adjust
            Variant variant = new Variant ( value );
            method.invoke ( _object, new Object [] { variant } );
        }
        else if ( pd.getPropertyType ().isAssignableFrom ( AnyValue.class ) )
        {
            AnyValue anyValue = new AnyValue ( type, value );
            method.invoke ( _object, new Object [] { anyValue } );
        }
        else
        {
            method.invoke ( _object, new Object [] { value } );
        }
    }

    public synchronized void connect ( Connector connector ) throws AlreadyConnectedException
    {
        if ( _connector != null )
            throw new AlreadyConnectedException ();
        
        _connector = connector;
    }
    
    public void disconnect ( Connector connector ) throws NotConnectedException
    {
        if ( _connector == connector )
        {
            _connector = null;
        }
        else
        {
            throw new NotConnectedException ();
        }
    }
}
