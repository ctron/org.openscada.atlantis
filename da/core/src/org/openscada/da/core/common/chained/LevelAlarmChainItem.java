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
    
    public static final String HIGH_ERROR = "org.openscada.da.level.high.error";
    public static final String LOW_ERROR = "org.openscada.da.level.low.error";
    
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
        attributes.put ( HIGH_ERROR, null );
        attributes.put ( LOW_ERROR, null );
        
        try
        {
            if ( !_highLevel.getValue ().isNull () && !value.isNull () )
                attributes.put ( HIGH_ALARM, new Variant ( value.asDouble () >= _highLevel.getValue ().asDouble () ) );
            
        }
        catch ( Exception e )
        {
            _log.info ( "Failed to evaluate high level alarm", e );
            attributes.put ( HIGH_ERROR, new Variant ( e.getMessage () ) );
        }
        
        try
        {
            if ( !_lowLevel.getValue().isNull () && !value.isNull () )
                attributes.put ( LOW_ALARM, new Variant ( value.asDouble () <= _lowLevel.getValue ().asDouble () ) );
        }
        catch ( Exception e )
        {
            _log.info ( "Failed to evaluate low level alarm", e );
            attributes.put ( LOW_ERROR, new Variant ( e.getMessage () ) );
        }
        
        addAttributes ( attributes );
    }
}
