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

public class SplitGroupProvider implements GroupProvider
{
    private NameProvider nameProvider = null;

    private String regex = "";

    private int skipPrefix = 0;

    private int skipSuffix = 0;

    public SplitGroupProvider ( final NameProvider nameProvider, final String regex )
    {
        this.nameProvider = nameProvider;
        this.regex = regex;
    }

    public SplitGroupProvider ( final NameProvider nameProvider, final String regex, final int skipPrefix, final int skipSuffix )
    {
        this.nameProvider = nameProvider;
        this.regex = regex;
        this.skipPrefix = skipPrefix;
        this.skipSuffix = skipSuffix;
    }

    public SplitGroupProvider ()
    {
    }

    public String[] getGrouping ( final ItemDescriptor descriptor )
    {
        if ( this.nameProvider == null )
        {
            return null;
        }

        final String name = this.nameProvider.getName ( descriptor );

        if ( name == null )
        {
            return null;
        }

        try
        {
            final String[] tok = name.split ( this.regex );
            if ( this.skipPrefix + this.skipSuffix >= tok.length )
            {
                return null;
            }
            final String[] result = new String[tok.length - ( this.skipPrefix + this.skipSuffix )];
            for ( int i = this.skipPrefix; i < tok.length - this.skipSuffix; i++ )
            {
                result[i - this.skipPrefix] = tok[i];
            }
            return result;
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    public void setNameProvider ( final NameProvider nameProvider )
    {
        this.nameProvider = nameProvider;
    }

    public void setRegex ( final String regex )
    {
        this.regex = regex;
    }

    public void setSkipPrefix ( final int skipPrefix )
    {
        this.skipPrefix = skipPrefix;
    }

    public void setSkipSuffix ( final int skipSuffix )
    {
        this.skipSuffix = skipSuffix;
    }

}
