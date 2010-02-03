/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.exec.splitter;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class SplitSplitter implements Splitter
{

    private final String pattern;

    public SplitSplitter ( final String pattern )
    {
        this.pattern = pattern;
    }

    public SplitResult split ( final String inputBuffer )
    {
        final SplitResult result = new SplitResult ();

        final StringTokenizer tok = new StringTokenizer ( inputBuffer, this.pattern, true );

        final List<String> resultList = new LinkedList<String> ();

        while ( tok.hasMoreTokens () )
        {
            final String token = tok.nextToken ();
            if ( !this.pattern.equals ( token ) )
            {
                if ( tok.hasMoreElements () )
                {
                    resultList.add ( token );
                }
                else
                {
                    result.setRemainingBuffer ( token );
                }
            }
        }

        result.setLines ( resultList.toArray ( new String[resultList.size ()] ) );

        return result;
    }
}
