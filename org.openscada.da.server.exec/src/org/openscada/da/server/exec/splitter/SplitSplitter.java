/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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
