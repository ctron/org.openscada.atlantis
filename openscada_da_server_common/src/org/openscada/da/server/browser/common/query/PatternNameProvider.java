/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.server.browser.common.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternNameProvider implements NameProvider
{
    private final NameProvider provider;

    private final Pattern pattern;

    private final int groupNumber;

    public PatternNameProvider ( final NameProvider provider, final Pattern pattern, final int groupNumber )
    {
        this.pattern = pattern;
        this.provider = provider;
        this.groupNumber = groupNumber;
    }

    public String getName ( final ItemDescriptor descriptor )
    {
        try
        {
            final String name = this.provider.getName ( descriptor );

            final Matcher m = this.pattern.matcher ( this.provider.getName ( descriptor ) );
            if ( m.matches () )
            {
                return m.group ( this.groupNumber );
            }
            return name;
        }
        catch ( final Throwable e )
        {
            return null;
        }
    }

}
