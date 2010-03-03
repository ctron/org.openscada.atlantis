package org.openscada.core.ice;

import org.openscada.core.Variant;

import OpenSCADA.Core.VariantBase;
import OpenSCADA.Core.VariantBoolean;
import OpenSCADA.Core.VariantDouble;
import OpenSCADA.Core.VariantInt32;
import OpenSCADA.Core.VariantInt64;
import OpenSCADA.Core.VariantString;
import OpenSCADA.Core.VariantType;

public class VariantHelper
{
    public static Variant fromIce ( final VariantBase variantBase )
    {
        if ( variantBase == null )
        {
            return null;
        }

        switch ( variantBase.vt.value () )
        {
        case VariantType._VTboolean:
        {
            return Variant.valueOf ( ( (VariantBoolean)variantBase ).value );
        }
        case VariantType._VTstring:
        {
            return new Variant ( ( (VariantString)variantBase ).value );
        }
        case VariantType._VTint64:
        {
            return new Variant ( ( (VariantInt64)variantBase ).value );
        }
        case VariantType._VTint32:
        {
            return new Variant ( ( (VariantInt32)variantBase ).value );
        }
        case VariantType._VTdouble:
        {
            return new Variant ( ( (VariantDouble)variantBase ).value );
        }
        case VariantType._VTnull:
        {
            return new Variant ();
        }
        default:
        {
            return null;
        }
        }
    }

    public static VariantBase toIce ( final Variant variant )
    {
        try
        {
            if ( variant == null )
            {
                return null;
            }

            if ( variant.isNull () )
            {
                return new VariantBase ( VariantType.VTnull );
            }
            else if ( variant.isBoolean () )
            {
                return new VariantBoolean ( VariantType.VTboolean, variant.asBoolean () );
            }
            else if ( variant.isString () )
            {
                return new VariantString ( VariantType.VTstring, variant.asString () );
            }
            else if ( variant.isInteger () )
            {
                return new VariantInt32 ( VariantType.VTint32, variant.asInteger () );
            }
            else if ( variant.isLong () )
            {
                return new VariantInt64 ( VariantType.VTint64, variant.asLong () );
            }
            else if ( variant.isDouble () )
            {
                return new VariantDouble ( VariantType.VTdouble, variant.asDouble () );
            }
        }
        catch ( final Exception e )
        {
        }
        return null;
    }
}
