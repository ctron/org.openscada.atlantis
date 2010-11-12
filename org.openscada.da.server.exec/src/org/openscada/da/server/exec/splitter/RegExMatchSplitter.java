/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegExMatchSplitter implements Splitter
{
    private final static Logger logger = LoggerFactory.getLogger ( RegExMatchSplitter.class );

    private final Pattern pattern;

    public RegExMatchSplitter ( final Pattern pattern )
    {
        this.pattern = pattern;
    }

    public SplitResult split ( final String inputBuffer )
    {
        final SplitResult result = new SplitResult ();

        final ArrayList<String> list = new ArrayList<String> ();

        boolean hadMatch = false;
        final Matcher m = this.pattern.matcher ( inputBuffer );
        logger.debug ( "Matcher: " + m );

        while ( m.find () )
        {
            hadMatch = true;
            list.add ( m.group () );
        }

        if ( hadMatch )
        {
            result.setLines ( list.toArray ( new String[0] ) );
            result.setRemainingBuffer ( inputBuffer.substring ( m.end () ) );
            return result;
        }
        else
        {
            return null;
        }
    }
}
