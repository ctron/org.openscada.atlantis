package org.openscada.da.core.common.chained;

import java.util.Map;

import org.openscada.da.core.WriteAttributesOperationListener.Results;
import org.openscada.da.core.WriteAttributesOperationListener.Result;
import org.openscada.da.core.data.Variant;

public class LevelAlarmChainItem implements InputChainItem
{

    private static final String HIGH_PRESET = "org.openscada.da.level.high.preset";
    private static final String LOW_PRESET = "org.openscada.da.level.low.preset";
    
    private static final String HIGH_ALARM = "org.openscada.da.level.high.alarm";
    private static final String LOW_ALARM = "org.openscada.da.level.low.alarm";
    
    private Double _highLevel = null;
    private Double _lowLevel = null;
    
    public void process ( Variant value, Map<String, Variant> attributes )
    {
        attributes.put ( HIGH_ALARM, null );
        attributes.put ( LOW_ALARM, null );
        
        try
        {
            if ( _highLevel != null )
                if ( value.asDouble () >= _highLevel )
                    attributes.put ( HIGH_ALARM, new Variant ( true ) );
            
        }
        catch ( Exception e )
        {
        }
        
        try
        {
            if ( _lowLevel != null )
                if ( value.asDouble () <= _lowLevel )
                    attributes.put ( LOW_ALARM, new Variant ( true ) );
        }
        catch ( Exception e )
        {
        }
    }

    public Results setAttributes ( Map<String, Variant> attributes )
    {
        Results results = new Results ();
        
        if ( attributes.containsKey ( HIGH_PRESET ) )
        {
            try
            {
                Variant value = attributes.get ( HIGH_PRESET );
                if ( value == null )
                    _highLevel = null;
                else
                    _highLevel = value.asDouble ();
                results.put ( HIGH_PRESET, new Result () );
            }
            catch ( Exception e )
            {
                results.put ( HIGH_PRESET, new Result ( e ) );
            }
        }
        
        if ( attributes.containsKey ( LOW_PRESET ) )
        {
            try
            {
                Variant value = attributes.get ( LOW_PRESET );
                if ( value == null )
                    _lowLevel = null;
                else
                    _lowLevel = value.asDouble ();
                results.put ( LOW_PRESET, new Result () );
            }
            catch ( Exception e )
            {
                results.put ( LOW_PRESET, new Result ( e ) );
            }
        }
        
        return results;
    }

}
