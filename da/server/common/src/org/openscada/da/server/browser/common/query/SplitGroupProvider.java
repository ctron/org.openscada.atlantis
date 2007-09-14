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

    public SplitGroupProvider ( NameProvider nameProvider, String regex )
    {
        _nameProvider = nameProvider;
        _regex = regex;
    }

    public SplitGroupProvider ()
    {
    }

    public String[] getGrouping ( ItemDescriptor descriptor )
    {
        if ( _nameProvider == null )
        {
            return null;
        }

        String name = _nameProvider.getName ( descriptor );

        if ( name == null )
        {
            return null;
        }

        try
        {
            String[] tok = name.split ( _regex );
            if ( _skipPrefix + _skipSuffix >= tok.length )
            {
                return null;
            }
            String[] result = new String[tok.length - ( _skipPrefix + _skipSuffix )];
            for ( int i = _skipPrefix; i < tok.length - _skipSuffix; i++ )
            {
                result[i - _skipPrefix] = tok[i];
            }
            return result;
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    public void setNameProvider ( NameProvider nameProvider )
    {
        _nameProvider = nameProvider;
    }

    public void setRegex ( String regex )
    {
        _regex = regex;
    }

    public void setSkipPrefix ( int skipPrefix )
    {
        _skipPrefix = skipPrefix;
    }

    public void setSkipSuffix ( int skipSuffix )
    {
        _skipSuffix = skipSuffix;
    }

}
