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
           _log.warn ( String.format ( "Failed to set value of property %s", _name ), e );
       }
    }
    
    protected void setValue ( Type type, Object value ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IntrospectionException
    {
        PropertyDescriptor pd = getPropertyDescriptor ();
        Method method = pd.getWriteMethod ();
        
        if ( method == null )
            return;
        
        if ( pd.getPropertyType ().isAssignableFrom ( Variant.class ) )
        {
            // we have a variant type so try to adjust
            Variant variant = new Variant ( value );
            method.invoke ( _object, new Object [] { variant } );
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
    
    public void disconnect ()
    {
        _connector = null;
    }
}
