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

package org.openscada.da.server.exec.extractor;

import java.util.List;
import java.util.StringTokenizer;

import org.openscada.da.server.exec.command.ExecutionResult;

public class SplitterExtractor extends AbstractArrayExtractor
{

    private final String pattern;

    public SplitterExtractor ( final String id, final String pattern, final List<FieldMapping> fields )
    {
        super ( id, fields );
        this.pattern = pattern;
    }

    @Override
    protected String[] getFields ( final ExecutionResult result )
    {
        final StringTokenizer tok = new StringTokenizer ( result.getOutput (), this.pattern );
        final String[] fields = new String[tok.countTokens ()];

        int i = 0;
        while ( tok.hasMoreElements () )
        {
            fields[i] = tok.nextToken ();
            i++;
        }

        return fields;
    }

}
