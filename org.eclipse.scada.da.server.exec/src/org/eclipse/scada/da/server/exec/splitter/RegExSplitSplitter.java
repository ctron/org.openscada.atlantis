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

package org.eclipse.scada.da.server.exec.splitter;

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
        final SplitResult result = new SplitResult ();

        final List<String> list = Arrays.asList ( this.pattern.split ( inputBuffer ) );

        if ( list.size () >= 2 )
        {
            final String last = list.remove ( list.size () );
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
