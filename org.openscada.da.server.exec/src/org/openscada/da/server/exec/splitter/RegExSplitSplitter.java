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

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class RegExSplitSplitter implements Splitter
{

    private final Pattern pattern;

    public RegExSplitSplitter ( final Pattern pattern )
    {
        this.pattern = pattern;
    }

    public SplitResult split ( final String inputBuffer )
    {
        SplitResult result = new SplitResult ();

        List<String> list = Arrays.asList ( this.pattern.split ( inputBuffer ) );

        if ( list.size () >= 2 )
        {
            String last = list.remove ( list.size () );
            result.setLines ( list.toArray ( new String[0] ) );
            result.setRemainingBuffer ( last );
        }
        else
        {
            result.setRemainingBuffer ( inputBuffer );
        }

        return result;
    }
}
