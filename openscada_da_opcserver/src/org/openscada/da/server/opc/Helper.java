/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.opc;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JICurrency;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.opc.dcom.common.KeyedResult;
import org.openscada.opc.dcom.da.OPCITEMDEF;
import org.openscada.opc.dcom.da.OPCITEMRESULT;
import org.openscada.opc.lib.da.browser.Access;

public class Helper
{
    private static Logger logger = Logger.getLogger ( Helper.class );

    public static Variant theirs2ours ( final JIVariant variant )
    {
        try
        {
            switch ( variant.getType () )
            {
            case JIVariant.VT_BOOL:
                return new Variant ( variant.getObjectAsBoolean () );
            case JIVariant.VT_EMPTY:
                return new Variant ();
            case JIVariant.VT_NULL:
                return new Variant ();
            case JIVariant.VT_I1:
                return new Variant ( ( (Character)variant.getObject () ).charValue () );
            case JIVariant.VT_I2:
                return new Variant ( variant.getObjectAsShort () );
            case JIVariant.VT_I4:
                return new Variant ( variant.getObjectAsInt () );
            case JIVariant.VT_INT:
                return new Variant ( variant.getObjectAsInt () );
            case JIVariant.VT_I8:
                return new Variant ( variant.getObjectAsLong () );
            case JIVariant.VT_R4:
                return new Variant ( variant.getObjectAsFloat () );
            case JIVariant.VT_R8:
                return new Variant ( variant.getObjectAsDouble () );
            case JIVariant.VT_BSTR:
            {
                final JIString str = variant.getObjectAsString ();
                if ( str != null )
                {
                    return new Variant ( str.getString () );
                }
                else
                {
                    return new Variant ( "" );
                }
            }
            case JIVariant.VT_UI1:
                return new Variant ( variant.getObjectAsUnsigned ().getValue ().byteValue () );
            case JIVariant.VT_UI2:
                return new Variant ( variant.getObjectAsUnsigned ().getValue ().shortValue () );
            case JIVariant.VT_UI4:
                return new Variant ( variant.getObjectAsUnsigned ().getValue ().intValue () );
            case JIVariant.VT_UINT:
                return new Variant ( variant.getObjectAsUnsigned ().getValue ().intValue () );
            case JIVariant.VT_VARIANT:
                return theirs2ours ( variant.getObjectAsVariant () );
            case JIVariant.VT_DATE:
                return new Variant ( variant.getObjectAsDate ().getTime () );
            case JIVariant.VT_CY:
            {
                final JICurrency c = (JICurrency)variant.getObject ();
                return new Variant ( ( (long)c.getUnits () << 32L | c.getFractionalUnits () ) / 10000L );
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
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        if ( entry.getErrorCode () != 0 )
        {
            attributes.put ( "opc.add.error", new Variant ( true ) );
            attributes.put ( "opc.add.code", new Variant ( entry.getErrorCode () ) );
            attributes.put ( "opc.add.message", new Variant ( String.format ( "0x%08x", entry.getErrorCode () ) ) );
        }
        return attributes;
    }

    public static Map<String, Variant> convertToAttributes ( final OPCITEMDEF def )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "opc.itemId", new Variant ( def.getItemID () ) );
        attributes.put ( "opc.clientHandle", new Variant ( def.getClientHandle () ) );
        if ( def.getAccessPath () != null )
        {
            attributes.put ( "opc.accessPath", new Variant ( def.getAccessPath () ) );
        }
        return attributes;
    }

    public static Map<String, Variant> convertToAttributes ( final OPCITEMRESULT result )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        attributes.put ( "opc.serverHandle", new Variant ( result.getServerHandle () ) );
        attributes.put ( "opc.dataType", new Variant ( result.getCanonicalDataType () ) );
        attributes.put ( "opc.accessRights", new Variant ( result.getAccessRights () ) );
        attributes.put ( "opc.accessRights.string", new Variant ( convertToAccessSet ( result.getAccessRights () ).toString () ) );
        return attributes;
    }

    public static Map<String, Variant> clearAttributes ()
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

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
