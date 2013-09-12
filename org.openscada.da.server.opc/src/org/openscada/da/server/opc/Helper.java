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

package org.openscada.da.server.opc;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.scada.core.Variant;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JICurrency;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.da.data.IODirection;
import org.openscada.opc.dcom.common.KeyedResult;
import org.openscada.opc.dcom.da.OPCITEMDEF;
import org.openscada.opc.dcom.da.OPCITEMRESULT;
import org.openscada.opc.lib.da.browser.Access;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helper
{

    private final static Logger logger = LoggerFactory.getLogger ( Helper.class );

    public static Variant theirs2ours ( final JIVariant variant )
    {
        try
        {
            switch ( variant.getType () )
            {
            case JIVariant.VT_BOOL:
                return Variant.valueOf ( variant.getObjectAsBoolean () );
            case JIVariant.VT_EMPTY:
                return Variant.NULL;
            case JIVariant.VT_NULL:
                return Variant.NULL;
            case JIVariant.VT_I1:
                return Variant.valueOf ( ( (Character)variant.getObject () ).charValue () );
            case JIVariant.VT_I2:
                return Variant.valueOf ( variant.getObjectAsShort () );
            case JIVariant.VT_I4:
                return Variant.valueOf ( variant.getObjectAsInt () );
            case JIVariant.VT_INT:
                return Variant.valueOf ( variant.getObjectAsInt () );
            case JIVariant.VT_I8:
                return Variant.valueOf ( variant.getObjectAsLong () );
            case JIVariant.VT_R4:
                return Variant.valueOf ( variant.getObjectAsFloat () );
            case JIVariant.VT_R8:
                return Variant.valueOf ( variant.getObjectAsDouble () );
            case JIVariant.VT_BSTR:
            {
                final JIString str = variant.getObjectAsString ();
                if ( str != null )
                {
                    return Variant.valueOf ( str.getString () );
                }
                else
                {
                    return Variant.valueOf ( "" );
                }
            }
            case JIVariant.VT_UI1:
                return Variant.valueOf ( variant.getObjectAsUnsigned ().getValue ().byteValue () );
            case JIVariant.VT_UI2:
                return Variant.valueOf ( variant.getObjectAsUnsigned ().getValue ().shortValue () );
            case JIVariant.VT_UI4:
                return Variant.valueOf ( variant.getObjectAsUnsigned ().getValue ().intValue () );
            case JIVariant.VT_UINT:
                return Variant.valueOf ( variant.getObjectAsUnsigned ().getValue ().intValue () );
            case JIVariant.VT_VARIANT:
                return theirs2ours ( variant.getObjectAsVariant () );
            case JIVariant.VT_DATE:
                return Variant.valueOf ( variant.getObjectAsDate ().getTime () );
            case JIVariant.VT_CY:
            {
                final JICurrency c = (JICurrency)variant.getObject ();
                return Variant.valueOf ( ( (long)c.getUnits () << 32L | c.getFractionalUnits () ) / 10000L );
            }
            default:
                return null;
            }
        }
        catch ( final JIException e )
        {
            logger.warn ( "Failed to convert", e );
            return null;
        }
    }

    public static JIVariant ours2theirs ( final Variant value )
    {
        try
        {
            if ( value.isNull () )
            {
                return null;
            }
            else if ( value.isBoolean () )
            {
                return new JIVariant ( value.asBoolean () );
            }
            else if ( value.isDouble () )
            {
                return new JIVariant ( value.asDouble () );
            }
            else if ( value.isInteger () )
            {
                return new JIVariant ( value.asInteger () );
            }
            else if ( value.isLong () )
            {
                return new JIVariant ( value.asLong () );
            }
            else if ( value.isString () )
            {
                return new JIVariant ( new JIString ( value.asString () ) );
            }
        }
        catch ( final Exception e )
        {
            logger.warn ( "Unable to convert write value", e );
            return null;
        }
        return null;
    }

    /**
     * Convert OPC access mask to OpenSCADA IODirection enum set
     * @param value the access mask
     * @return the enum set
     */
    public static EnumSet<IODirection> convertToAccessSet ( final int value )
    {
        final EnumSet<IODirection> set = EnumSet.noneOf ( IODirection.class );

        if ( ( value & Access.READ.getCode () ) > 0 )
        {
            set.add ( IODirection.INPUT );
        }
        if ( ( value & Access.WRITE.getCode () ) > 0 )
        {
            set.add ( IODirection.OUTPUT );
        }

        return set;
    }

    public static Map<String, Variant> convertToAttributes ( final KeyedResult<OPCITEMDEF, OPCITEMRESULT> entry )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ( 4 );
        if ( entry.getErrorCode () != 0 )
        {
            attributes.put ( "opc.add.error", Variant.TRUE );
            attributes.put ( "opc.add.code", Variant.valueOf ( entry.getErrorCode () ) );
            attributes.put ( "opc.add.message", Variant.valueOf ( String.format ( "0x%08x", entry.getErrorCode () ) ) );
        }
        return attributes;
    }

    public static Map<String, Variant> convertToAttributes ( final OPCITEMDEF def )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ( 4 );
        attributes.put ( "opc.itemId", Variant.valueOf ( def.getItemID () ) );
        attributes.put ( "opc.clientHandle", Variant.valueOf ( def.getClientHandle () ) );
        if ( def.getAccessPath () != null )
        {
            attributes.put ( "opc.accessPath", Variant.valueOf ( def.getAccessPath () ) );
        }
        return attributes;
    }

    public static Map<String, Variant> convertToAttributes ( final OPCITEMRESULT result )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ( 4 );

        attributes.put ( "opc.serverHandle", Variant.valueOf ( result.getServerHandle () ) );
        attributes.put ( "opc.dataType", Variant.valueOf ( result.getCanonicalDataType () ) );
        attributes.put ( "opc.accessRights", Variant.valueOf ( result.getAccessRights () ) );
        attributes.put ( "opc.accessRights.string", Variant.valueOf ( convertToAccessSet ( result.getAccessRights () ).toString () ) );
        return attributes;
    }

    public static Map<String, Variant> clearAttributes ()
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ( 16 );

        attributes.put ( "opc.serverHandle", null );
        attributes.put ( "opc.dataType", null );
        attributes.put ( "opc.accessRights", null );
        attributes.put ( "opc.accessRights.string", null );
        attributes.put ( "opc.accessPath", null );
        attributes.put ( "opc.clientHandle", null );
        attributes.put ( "opc.add.error", null );
        attributes.put ( "opc.add.code", null );
        attributes.put ( "opc.add.message", null );

        return attributes;
    }
}
