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
