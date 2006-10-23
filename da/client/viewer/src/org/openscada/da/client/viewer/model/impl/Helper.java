package org.openscada.da.client.viewer.model.impl;

import java.util.EnumSet;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.RGB;
import org.openscada.common.VariantType;
import org.openscada.core.Variant;
import org.openscada.da.client.viewer.model.Type;
import org.openscada.da.client.viewer.model.types.Color;

public class Helper
{
    private static Logger _log = Logger.getLogger ( Helper.class );
    
    @SuppressWarnings("unchecked")
    public static EnumSet<Type> classToType ( Class clazz )
    {
        EnumSet<Type> set = EnumSet.noneOf ( Type.class );
        
        _log.debug ( String.format ( "Class to type: %s", clazz ) );
        
        if ( clazz.isAssignableFrom ( String.class ) )
        {
            set.add ( Type.STRING );
            set.add ( Type.NULL );
        }
        if ( clazz.isAssignableFrom ( Boolean.class ) )
        {
            set.add ( Type.BOOLEAN );
            set.add ( Type.NULL );
        }
        if ( clazz.isAssignableFrom ( Double.class ) )
        {
            set.add ( Type.DOUBLE );
            set.add ( Type.NULL );
        }
        if ( clazz.isAssignableFrom ( Color.class ) )
        {
            set.add ( Type.COLOR );
            set.add ( Type.NULL );
        }
        if ( clazz.isAssignableFrom ( Long.class ) )
        {
            set.add ( Type.INTEGER );
            set.add ( Type.NULL );
        }
        if ( clazz.isAssignableFrom ( Variant.class ) )
        {
            set.add ( Type.VARIANT );
            set.add ( Type.STRING );
            set.add ( Type.BOOLEAN );
            set.add ( Type.DOUBLE );
            set.add ( Type.INTEGER );
            set.add ( Type.NULL );
        }
        if ( clazz.isAssignableFrom ( AnyValue.class ) )
        {
            set.addAll ( EnumSet.allOf ( Type.class ) );
        }
        
        if ( _log.isDebugEnabled () )
        {
            for ( Type type : set )
            {
                _log.debug ( "Matches: " + type.name () );
            }
        }
        
        return set;
    }
    
    public static Variant fromXML ( VariantType variantType )
    {
        if ( variantType.getBoolean () != null )
        {
            return new Variant ( variantType.getBoolean ().getBooleanValue () );
        }
        else if ( variantType.getDouble () != null )
        {
            return new Variant ( variantType.getDouble ().getDoubleValue () );
        }
        else if ( variantType.getInt32 () != null )
        {
            return new Variant ( variantType.getInt32 ().getIntValue () );
        }
        else if ( variantType.getInt64 () != null )
        {
            return new Variant ( variantType.getInt64 ().getLongValue () );
        }
        else if ( variantType.getString () != null )
        {
            return new Variant ( variantType.getString () );
        }
        else if ( variantType.getNull () != null )
        {
            return new Variant ();
        }
        else
        {
            return null;
        }
    }
    
    public static RGB colorToRGB ( Color color )
    {
        return new RGB ( color.getRed (), color.getGreen (), color.getBlue () ); 
    }
    
    public static Color colorFromRGB ( RGB rgb )
    {
        return new Color ( rgb.red, rgb.green, rgb.blue );
    }
}
