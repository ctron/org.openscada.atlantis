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

package org.openscada.da.server.browser.common.query;

import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Pattern;

import org.openscada.utils.str.StringHelper;

public class SplitNameProvider implements NameProvider
{

    private final Pattern pattern;

    private final NameProvider nameProvider;

    private final int fromStart;

    private final int fromEnd;

    private final String delimiter;

    public SplitNameProvider ( final NameProvider nameProvider, final Pattern pattern, final int fromStart, final int fromEnd, final String delimiter )
    {
        this.nameProvider = nameProvider;
        this.pattern = pattern;
        this.fromStart = fromStart;
        this.fromEnd = fromEnd;
        this.delimiter = delimiter;
    }

    public SplitNameProvider ( final NameProvider nameProvider, final String pattern, final int fromStart, final int fromEnd, final String delimiter )
    {
        this ( nameProvider, Pattern.compile ( pattern ), fromStart, fromEnd, delimiter );
    }

    public String getName ( final ItemDescriptor descriptor )
    {
        final String name = this.nameProvider.getName ( descriptor );

        final Collection<String> result = new LinkedList<String> ();
        final String[] toks = this.pattern.split ( name );
        for ( int i = 0; i < toks.length; i++ )
        {
            if ( i < this.fromStart || i >= toks.length - this.fromEnd )
            {
                result.add ( toks[i] );
            }
        }

        return StringHelper.join ( result, this.delimiter );
    }
}
