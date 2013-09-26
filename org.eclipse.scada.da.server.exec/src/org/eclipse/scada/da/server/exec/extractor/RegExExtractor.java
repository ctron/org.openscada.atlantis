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

package org.eclipse.scada.da.server.exec.extractor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.scada.da.server.exec.command.ExecutionResult;

public class RegExExtractor extends AbstractArrayExtractor
{

    private final Pattern pattern;

    private final boolean requireFullMatch;

    public RegExExtractor ( final String id, final Pattern pattern, final boolean requireFullMatch, final List<FieldMapping> groups )
    {
        super ( id, groups );
        this.pattern = pattern;
        this.requireFullMatch = requireFullMatch;
    }

    @Override
    protected String[] getFields ( final ExecutionResult result )
    {
        final Matcher m = this.pattern.matcher ( result.getOutput () );
        if ( this.requireFullMatch )
        {
            if ( !m.matches () )
            {
                throw new RuntimeException ( "Failed to match input" );
            }
            return convertToResult ( m );
        }
        else
        {
            if ( !m.find () )
            {
                throw new RuntimeException ( "Failed to match input" );
            }
            return convertToResult ( m );
        }
    }

    private String[] convertToResult ( final Matcher m )
    {
        final String[] result = new String[m.groupCount () + 1];
        for ( int i = 0; i < result.length; i++ )
        {
            result[i] = m.group ( i );
        }
        return result;
    }

}
