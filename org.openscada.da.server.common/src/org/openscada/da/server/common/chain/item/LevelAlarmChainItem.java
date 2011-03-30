/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.common.chain.item;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.chain.BaseChainItemCommon;
import org.openscada.da.server.common.chain.BooleanBinder;
import org.openscada.da.server.common.chain.VariantBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LevelAlarmChainItem extends BaseChainItemCommon
{

    private final static Logger logger = LoggerFactory.getLogger ( LevelAlarmChainItem.class );

    public static final String HIGH_ACTIVE = "org.openscada.da.level.high.active";

    public static final String HIGHHIGH_ACTIVE = "org.openscada.da.level.highhigh.active";

    public static final String LOW_ACTIVE = "org.openscada.da.level.low.active";

    public static final String LOWLOW_ACTIVE = "org.openscada.da.level.lowlow.active";

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

    private final VariantBinder highLevel = new VariantBinder ( Variant.NULL );

    private final VariantBinder lowLevel = new VariantBinder ( Variant.NULL );

    private final VariantBinder highHighLevel = new VariantBinder ( Variant.NULL );

    private final VariantBinder lowLowLevel = new VariantBinder ( Variant.NULL );

    private final BooleanBinder highActive = new BooleanBinder ();

    private final BooleanBinder highHighActive = new BooleanBinder ();

    private final BooleanBinder lowActive = new BooleanBinder ();

    private final BooleanBinder lowLowActive = new BooleanBinder ();

    public LevelAlarmChainItem ( final HiveServiceRegistry serviceRegistry )
    {
        super ( serviceRegistry );

        addBinder ( HIGH_PRESET, this.highLevel );
        addBinder ( LOW_PRESET, this.lowLevel );
        addBinder ( HIGHHIGH_PRESET, this.highHighLevel );
        addBinder ( LOWLOW_PRESET, this.lowLowLevel );

        addBinder ( HIGH_ACTIVE, this.highActive );
        addBinder ( LOW_ACTIVE, this.lowActive );
        addBinder ( HIGHHIGH_ACTIVE, this.highHighActive );
        addBinder ( LOWLOW_ACTIVE, this.lowLowActive );

        setReservedAttributes ( HIGH_ALARM, LOW_ALARM, HIGHHIGH_ALARM, LOWLOW_ALARM );
    }

    @Override
    public Variant process ( final Variant value, final Map<String, Variant> attributes )
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
            if ( !this.highLevel.getValue ().isNull () && !value.isNull () && this.highActive.getSafeValue ( false ) )
            {
                attributes.put ( HIGH_ALARM, Variant.valueOf ( value.asDouble () >= this.highLevel.getValue ().asDouble () ) );
            }

        }
        catch ( final Throwable e )
        {
            logger.info ( "Failed to evaluate high level alarm", e );
            attributes.put ( HIGH_ERROR, Variant.valueOf ( e.getMessage () ) );
        }

        // low alarm
        try
        {
            if ( !this.lowLevel.getValue ().isNull () && !value.isNull () && this.lowActive.getSafeValue ( false ) )
            {
                attributes.put ( LOW_ALARM, Variant.valueOf ( value.asDouble () <= this.lowLevel.getValue ().asDouble () ) );
            }
        }
        catch ( final Throwable e )
        {
            logger.info ( "Failed to evaluate low level alarm", e );
            attributes.put ( LOW_ERROR, Variant.valueOf ( e.getMessage () ) );
        }

        // high high alarm
        try
        {
            if ( !this.highHighLevel.getValue ().isNull () && !value.isNull () && this.highHighActive.getSafeValue ( false ) )
            {
                attributes.put ( HIGHHIGH_ALARM, Variant.valueOf ( value.asDouble () >= this.highHighLevel.getValue ().asDouble () ) );
            }

        }
        catch ( final Throwable e )
        {
            logger.info ( "Failed to evaluate high high level alarm", e );
            attributes.put ( HIGHHIGH_ERROR, Variant.valueOf ( e.getMessage () ) );
        }

        // low low alarm
        try
        {
            if ( !this.lowLowLevel.getValue ().isNull () && !value.isNull () && this.lowLowActive.getSafeValue ( false ) )
            {
                attributes.put ( LOWLOW_ALARM, Variant.valueOf ( value.asDouble () <= this.lowLowLevel.getValue ().asDouble () ) );
            }
        }
        catch ( final Throwable e )
        {
            logger.info ( "Failed to evaluate low low level alarm", e );
            attributes.put ( LOWLOW_ERROR, Variant.valueOf ( e.getMessage () ) );
        }

        // add our attributes
        addAttributes ( attributes );

        // no change
        return null;
    }
}
