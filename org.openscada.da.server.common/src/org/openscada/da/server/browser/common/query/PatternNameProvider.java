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
