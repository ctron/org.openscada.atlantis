package org.openscada.da.core.common.chained;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.da.core.data.Variant;

public class LevelAlarmChainItem extends InputChainItemCommon
{
    private static Logger _log = Logger.getLogger ( LevelAlarmChainItem.class );
    
    public static final String HIGH_PRESET = "org.openscada.da.level.high.preset";
    public static final String LOW_PRESET = "org.openscada.da.level.low.preset";
    
    public static final String HIGH_ALARM = "org.openscada.da.level.high.alarm";
    public static final String LOW_ALARM = "org.openscada.da.level.low.alarm";
    
    private VariantBinder _highLevel = new VariantBinder ( new Variant () );
    private VariantBinder _lowLevel = new VariantBinder ( new Variant () );
    
    public LevelAlarmChainItem ()
    {
        super ();
        addBinder ( HIGH_PRESET, _highLevel );
        addBinder ( LOW_PRESET, _lowLevel );
        
        setReservedAttributes ( HIGH_ALARM, LOW_ALARM );
    }
    
    public void process ( Variant value, Map<String, Variant> attributes )
    {
        attributes.put ( HIGH_ALARM, null );
        attributes.put ( LOW_ALARM, null );
        
        try
        {
            if ( !_highLevel.getValue ().isNull () && !value.isNull () )
                if ( value.asDouble () >= _highLevel.getValue ().asDouble () )
                    attributes.put ( HIGH_ALARM, new Variant ( true ) );
            
        }
        catch ( Exception e )
        {
            _log.info ( "Failed to evaluate high level alarm", e );
        }
        
        try
        {
            if ( !_lowLevel.getValue().isNull () && !value.isNull () )
                if ( value.asDouble () <= _lowLevel.getValue ().asDouble () )
                    attributes.put ( LOW_ALARM, new Variant ( true ) );
        }
        catch ( Exception e )
        {
            _log.info ( "Failed to evaluate low level alarm", e );
        }
        
        addAttributes ( attributes );
    }
}
