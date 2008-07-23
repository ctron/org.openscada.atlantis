/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.chain.item;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.chain.BaseChainItemCommon;
import org.openscada.da.server.common.chain.VariantBinder;

public class LevelAlarmChainItem extends BaseChainItemCommon
{
    private static Logger _log = Logger.getLogger ( LevelAlarmChainItem.class );

    public static final String HIGH_PRESET = "org.openscada.da.level.high.preset";
    public static final String HIGHHIGH_PRESET = "org.openscada.da.level.highhigh.preset";
    public static final String LOW_PRESET = "org.openscada.da.level.low.preset";
    public static final String LOWLOW_PRESET = "org.openscada.da.level.lowlow.preset";

    public static final String HIGH_ALARM = "org.openscada.da.level.high.alarm";
    public static final String HIGHHIGH_ALARM = "org.openscada.da.level.highhigh.alarm";
    public static final String LOWLOW_ALARM = "org.openscada.da.level.lowlow.alarm";
    public static final String LOW_ALARM = "org.openscada.da.level.low.alarm";

    public static final String HIGH_ERROR = "org.openscada.da.level.high.error";
    public static final String HIGHHIGH_ERROR = "org.openscada.da.level.highhigh.error";
    public static final String LOW_ERROR = "org.openscada.da.level.low.error";
    public static final String LOWLOW_ERROR = "org.openscada.da.level.lowlow.error";

    private VariantBinder _highLevel = new VariantBinder ( new Variant () );
    private VariantBinder _lowLevel = new VariantBinder ( new Variant () );
    private VariantBinder _highHighLevel = new VariantBinder ( new Variant () );
    private VariantBinder _lowLowLevel = new VariantBinder ( new Variant () );

    public LevelAlarmChainItem ( HiveServiceRegistry serviceRegistry )
    {
        super ( serviceRegistry );

        addBinder ( HIGH_PRESET, _highLevel );
        addBinder ( LOW_PRESET, _lowLevel );
        addBinder ( HIGHHIGH_PRESET, _highHighLevel );
        addBinder ( LOWLOW_PRESET, _lowLowLevel );

        setReservedAttributes ( HIGH_ALARM, LOW_ALARM, HIGHHIGH_ALARM, LOWLOW_ALARM );
    }

    public void process ( Variant value, Map<String, Variant> attributes )
    {
        attributes.put ( HIGH_ALARM, null );
        attributes.put ( LOW_ALARM, null );
        attributes.put ( HIGH_ERROR, null );
        attributes.put ( LOW_ERROR, null );
        
        attributes.put ( HIGHHIGH_ALARM, null );
        attributes.put ( LOWLOW_ALARM, null );
        attributes.put ( HIGHHIGH_ERROR, null );
        attributes.put ( LOWLOW_ERROR, null );

        // high alarm
        try
        {
            if ( !_highLevel.getValue ().isNull () && !value.isNull () )
            {
                attributes.put ( HIGH_ALARM, new Variant ( value.asDouble () >= _highLevel.getValue ().asDouble () ) );
            }

        }
        catch ( Throwable e )
        {
            _log.info ( "Failed to evaluate high level alarm", e );
            attributes.put ( HIGH_ERROR, new Variant ( e.getMessage () ) );
        }
        
        // low alarm
        try
        {
            if ( !_lowLevel.getValue ().isNull () && !value.isNull () )
            {
                attributes.put ( LOW_ALARM, new Variant ( value.asDouble () <= _lowLevel.getValue ().asDouble () ) );
            }
        }
        catch ( Throwable e )
        {
            _log.info ( "Failed to evaluate low level alarm", e );
            attributes.put ( LOW_ERROR, new Variant ( e.getMessage () ) );
        }
        
        // high high alarm
        try
        {
            if ( !_highHighLevel.getValue ().isNull () && !value.isNull () )
            {
                attributes.put ( HIGHHIGH_ALARM, new Variant ( value.asDouble () >= _highHighLevel.getValue ().asDouble () ) );
            }

        }
        catch ( Throwable e )
        {
            _log.info ( "Failed to evaluate high high level alarm", e );
            attributes.put ( HIGHHIGH_ERROR, new Variant ( e.getMessage () ) );
        }
        
        // low low alarm
        try
        {
            if ( !_lowLowLevel.getValue ().isNull () && !value.isNull () )
            {
                attributes.put ( LOWLOW_ALARM, new Variant ( value.asDouble () <= _lowLowLevel.getValue ().asDouble () ) );
            }
        }
        catch ( Throwable e )
        {
            _log.info ( "Failed to evaluate low low level alarm", e );
            attributes.put ( LOWLOW_ERROR, new Variant ( e.getMessage () ) );
        }

        // add our attributes
        addAttributes ( attributes );
    }
}
