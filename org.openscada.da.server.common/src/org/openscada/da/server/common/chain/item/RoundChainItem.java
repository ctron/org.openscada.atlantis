/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

import org.openscada.core.Variant;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.chain.BaseChainItemCommon;
import org.openscada.da.server.common.chain.StringBinder;

/**
 * This class is a chain item which performs rounding
 * @author Jens Reimann
 *
 */
public class RoundChainItem extends BaseChainItemCommon
{
    public static final String ROUND_BASE = "org.openscada.da.round";

    public static final String ORIGINAL_VALUE = ROUND_BASE + ".value.original";

    public static final String ROUND_ACTIVE = ROUND_BASE + ".active";

    public static final String ROUND_TYPE = ROUND_BASE + ".type";

    public static final String ROUND_ERROR = ROUND_BASE + ".error";

    private final StringBinder roundType = new StringBinder ();

    private enum RoundType
    {
        NONE,
        CEIL,
        FLOOR,
        ROUND,
    }

    public RoundChainItem ( final HiveServiceRegistry serviceRegistry )
    {
        super ( serviceRegistry );

        addBinder ( ROUND_TYPE, this.roundType );
        setReservedAttributes ( ORIGINAL_VALUE, ROUND_ACTIVE, ROUND_ERROR );
    }

    public Variant process ( final Variant value, final Map<String, Variant> attributes )
    {
        attributes.put ( ROUND_ACTIVE, null );
        attributes.put ( ORIGINAL_VALUE, null );
        attributes.put ( ROUND_ERROR, null );
        attributes.put ( ROUND_TYPE, null );

        final Variant originalValue = new Variant ( value );

        // check and get the rounding type
        final RoundType type = checkRoundType ( attributes );

        Double doubleValue = null;

        // check if we got a double value
        try
        {
            if ( value.isDouble () )
            {
                doubleValue = value.asDouble ();
            }
        }
        catch ( final Throwable e )
        {
        }

        // add binder attributes
        addAttributes ( attributes );

        Variant newValue = null;

        // common section
        if ( type != RoundType.NONE && doubleValue != null )
        {
            newValue = performRound ( type, doubleValue, value, attributes );
            attributes.put ( ROUND_ACTIVE, Variant.valueOf ( true ) );
            attributes.put ( ORIGINAL_VALUE, originalValue );
            attributes.put ( ROUND_TYPE, new Variant ( type.toString () ) );
        }
        else if ( type == RoundType.NONE )
        {
            attributes.put ( ROUND_TYPE, null );
        }

        return newValue;
    }

    private Variant performRound ( final RoundType type, final double doubleValue, final Variant value, final Map<String, Variant> attributes )
    {
        // round
        switch ( type )
        {
        case NONE:
            return null;
        case CEIL:
            return new Variant ( Math.ceil ( doubleValue ) );
        case FLOOR:
            return new Variant ( Math.floor ( doubleValue ) );
        case ROUND:
            return new Variant ( (double)Math.round ( doubleValue ) );
        }

        return null;
    }

    private RoundType checkRoundType ( final Map<String, Variant> attributes )
    {
        // not value is set ... so default to "NONE"
        if ( this.roundType.getValue () == null )
        {
            return RoundType.NONE;
        }

        try
        {
            final RoundType type = RoundType.valueOf ( this.roundType.getValue ().toUpperCase () );
            if ( type != null )
            {
                return type;
            }
        }
        catch ( final Throwable e )
        {
            attributes.put ( ROUND_ERROR, new Variant ( e.getMessage () ) );
        }
        return RoundType.NONE;
    }
}
