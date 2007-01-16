package org.openscada.core.ice;

import org.openscada.core.Variant;

import OpenSCADA.Core.VariantBase;
import OpenSCADA.Core.VariantBoolean;
import OpenSCADA.Core.VariantString;
import OpenSCADA.Core.VariantType;

public class VariantHelper
{
    public static Variant fromIce ( VariantBase variantBase )
    {
        switch ( variantBase.vt.value () )
        {
        case VariantType._VTboolean:
        {
            return new Variant ( ((VariantBoolean)variantBase).value );
        }
        case VariantType._VTstring:
            return new Variant ( ((VariantString)variantBase).value );
        default:
        {
            return null;
        }
        }
    }

    public static VariantBase toIce ( Variant variant )
    {
        try
        {
            if ( variant.isNull () )
            {
                return null;
            }
            else if ( variant.isBoolean () )
            {
                return new VariantBoolean ( VariantType.VTboolean, variant.asBoolean () );
            }
            else if ( variant.isString () )
            {
                return new VariantString ( VariantType.VTstring, variant.asString () );
            }
        }
        catch ( Exception e )
        {
        }
        return null;
    }
}
