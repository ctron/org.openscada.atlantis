package org.openscada.da.core.common.chained;

import java.util.Map;

import org.openscada.da.core.data.Variant;

public class ScaleInputItem extends InputChainItemCommon
{
    public static final String SCALE_FACTOR = "org.openscada.da.scale.factor";
    public static final String SCALE_RAW = "org.openscada.da.scale.raw";
    public static final String SCALE_ERROR = "org.openscada.da.scale.error";
    
    private VariantBinder _scaleFactor = new VariantBinder ( new Variant () );
    
    public ScaleInputItem ()
    {
        super ();
     
        addBinder ( SCALE_FACTOR, _scaleFactor );
        setReservedAttributes ( SCALE_RAW, SCALE_ERROR );
    }
    
    public void process ( Variant value, Map<String, Variant> attributes )
    {
        attributes.put ( SCALE_RAW, null );
        attributes.put ( SCALE_ERROR, null );
        try
        {
            Variant scaleFactor = _scaleFactor.getValue ();
            // only process if we have a scale factor
            if ( !scaleFactor.isNull () )
            {
                attributes.put ( SCALE_RAW, new Variant ( value ) );
                value.setValue ( value.asDouble () * scaleFactor.asDouble () );
            }
        }
        catch ( Exception e )
        {
            attributes.put ( SCALE_ERROR, new Variant ( e.getMessage () ) );
        }
        
        addAttributes ( attributes );
    }
    
    
}
