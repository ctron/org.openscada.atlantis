package org.openscada.da.core.common.chained;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.da.core.WriteAttributesOperationListener.Results;
import org.openscada.da.core.WriteAttributesOperationListener.Result;
import org.openscada.da.core.data.Variant;

public class LevelAlarmChainItem implements InputChainItem
{
    private static Logger _log = Logger.getLogger ( LevelAlarmChainItem.class );
    
    public static final String HIGH_PRESET = "org.openscada.da.level.high.preset";
    public static final String LOW_PRESET = "org.openscada.da.level.low.preset";
    
    public static final String HIGH_ALARM = "org.openscada.da.level.high.alarm";
    public static final String LOW_ALARM = "org.openscada.da.level.low.alarm";
    
    private Double _highLevel = null;
    private Double _lowLevel = null;
    
    public void process ( Variant value, Map<String, Variant> attributes )
    {
        attributes.put ( HIGH_ALARM, null );
        attributes.put ( LOW_ALARM, null );
        
        try
        {
            if ( _highLevel != null && !value.isNull () )
                if ( value.asDouble () >= _highLevel )
                    attributes.put ( HIGH_ALARM, new Variant ( true ) );
            
        }
        catch ( Exception e )
        {
            _log.info ( "Failed to evaluate high level alarm", e );
        }
        
        try
        {
            if ( _lowLevel != null && !value.isNull () )
                if ( value.asDouble () <= _lowLevel )
                    attributes.put ( LOW_ALARM, new Variant ( true ) );
        }
        catch ( Exception e )
        {
            _log.info ( "Failed to evaluate low level alarm", e );
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
                {
                    if ( value.isNull () )
                        _highLevel = null;
                    else
                        _highLevel = value.asDouble ();
                }
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
                {
                    if ( value.isNull () )
                        _lowLevel = null;
                    else
                        _lowLevel = value.asDouble ();
                }
                results.put ( LOW_PRESET, new Result () );
            }
            catch ( Exception e )
            {
                results.put ( LOW_PRESET, new Result ( e ) );
            }
        }
        
        if ( attributes.containsKey ( HIGH_ALARM ) )
        {
            results.put ( HIGH_ALARM, new Result ( new Exception ( "Attribute may not be overwritten ") ) );
        }
        if ( attributes.containsKey ( LOW_ALARM ) )
        {
            results.put ( LOW_ALARM, new Result ( new Exception ( "Attribute may not be overwritten ") ) );
        }
        
        return results;
    }

}
