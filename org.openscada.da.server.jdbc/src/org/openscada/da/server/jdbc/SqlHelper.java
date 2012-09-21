/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.jdbc;

import java.nio.CharBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// FIXME: duplicate of org.openscada.utils.osgi.jdbc.task.SqlHelper, move to aurora
public final class SqlHelper
{

    private final static Logger logger = LoggerFactory.getLogger ( SqlHelper.class );

    private SqlHelper ()
    {
    }

    /**
     * Expand parameters for statement from named parameter position map
     * 
     * @param posMap
     *            the position map
     * @param parameters
     *            the named parameters map
     * @return the expanded positional parameters
     */
    public static Object[] expandParameters ( final Map<String, List<Integer>> posMap, final Map<String, Object> parameters )
    {
        // find max
        int max = -1;
        for ( final Map.Entry<String, List<Integer>> entry : posMap.entrySet () )
        {
            for ( final Integer i : entry.getValue () )
            {
                max = Math.max ( max, i );
            }
            if ( !parameters.containsKey ( entry.getKey ().toUpperCase () ) )
            {
                throw new IllegalArgumentException ( String.format ( "Named parameter %s could not be found in parameters", entry.getKey () ) );
            }
        }

        logger.trace ( "Max parameter: {}", max );
        if ( max < 0 )
        {
            return new Object[] {};
        }

        final Object[] result = new Object[max + 1];

        for ( final Map.Entry<String, List<Integer>> entry : posMap.entrySet () )
        {
            final Object value = parameters.get ( entry.getKey ().toUpperCase () );
            for ( final Integer i : entry.getValue () )
            {
                result[i] = value;
            }
        }
        return result;
    }

    private static boolean nextEquals ( final CharBuffer buffer, final char next )
    {
        if ( !buffer.hasRemaining () )
        {
            return false;
        }
        return buffer.get ( buffer.position () ) == next;
    }

    private static enum ParseState
    {
        NORMAL,
        QUOTE,
        NAME;
    }

    private static boolean isIdentifier ( final char ch )
    {
        return Character.isLetterOrDigit ( ch ) || ( ch == '_' );
    }

    public static String convertSql ( final String sql, final Map<String, List<Integer>> posMap )
    {
        final int length = sql.length ();
        final StringBuffer result = new StringBuffer ( length );

        final CharBuffer cb = CharBuffer.wrap ( sql );

        ParseState parseState = ParseState.NORMAL;
        StringBuilder name = null;
        int currentPosition = 0;

        while ( cb.hasRemaining () )
        {
            final char c = cb.get ();

            switch ( parseState )
            {
            case NAME:
            {
                if ( isIdentifier ( c ) )
                {
                    name.append ( c );
                }
                else
                {
                    parseState = ParseState.NORMAL;
                    result.append ( c );

                    addToPosMap ( posMap, name, currentPosition );
                    currentPosition++;
                }
            }
                break;
            case QUOTE:
            {
                switch ( c )
                {
                case '\'':
                    if ( nextEquals ( cb, '\'' ) )
                    {
                        result.append ( c );
                        cb.get (); // eat next
                    }
                    else
                    {
                        parseState = ParseState.NORMAL;
                        result.append ( c );
                    }
                    break;
                default:
                    result.append ( c );
                    break;
                }
            }
                break;

            case NORMAL:
            {
                switch ( c )
                {
                case '\'':
                    parseState = ParseState.QUOTE;
                    result.append ( c );
                    break;
                case ':':
                    parseState = ParseState.NAME;
                    result.append ( '?' );
                    name = new StringBuilder ();
                    break;
                default:
                    result.append ( c );
                    break;
                }
            }
                break;
            }

        }

        switch ( parseState )
        {
        case NAME:
            logger.trace ( "Completing in status NAME, add remainder" );
            addToPosMap ( posMap, name, currentPosition );
            break;

        default:
            break;
        }

        return result.toString ();
    }

    private static void addToPosMap ( final Map<String, List<Integer>> posMap, final StringBuilder name, final int currentPosition )
    {
        final String key = name.toString ().toUpperCase ();

        // add to position map
        List<Integer> indexes = posMap.get ( key );
        if ( indexes == null )
        {
            indexes = new LinkedList<Integer> ();
            posMap.put ( key, indexes );
        }
        indexes.add ( currentPosition );
        logger.trace ( "Adding {} as parameter #{}", key, currentPosition );
    }

}
