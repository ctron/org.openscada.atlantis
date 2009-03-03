/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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
    private NameProvider _nameProvider = null;

    private String _regex = "";

    private int _skipPrefix = 0;

    private int _skipSuffix = 0;

    public SplitGroupProvider ( final NameProvider nameProvider, final String regex )
    {
        this._nameProvider = nameProvider;
        this._regex = regex;
    }

    public SplitGroupProvider ()
    {
    }

    public String[] getGrouping ( final ItemDescriptor descriptor )
    {
        if ( this._nameProvider == null )
        {
            return null;
        }

        final String name = this._nameProvider.getName ( descriptor );

        if ( name == null )
        {
            return null;
        }

        try
        {
            final String[] tok = name.split ( this._regex );
            if ( this._skipPrefix + this._skipSuffix >= tok.length )
            {
                return null;
            }
            final String[] result = new String[tok.length - ( this._skipPrefix + this._skipSuffix )];
            for ( int i = this._skipPrefix; i < tok.length - this._skipSuffix; i++ )
            {
                result[i - this._skipPrefix] = tok[i];
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
        this._nameProvider = nameProvider;
    }

    public void setRegex ( final String regex )
    {
        this._regex = regex;
    }

    public void setSkipPrefix ( final int skipPrefix )
    {
        this._skipPrefix = skipPrefix;
    }

    public void setSkipSuffix ( final int skipSuffix )
    {
        this._skipSuffix = skipSuffix;
    }

}
