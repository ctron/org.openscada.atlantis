/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 TH4 SYSTEMS GmbH (http://inavare.com)
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

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SplitGroupProvider implements GroupProvider
{

    private final static Logger logger = LoggerFactory.getLogger ( SplitGroupProvider.class );

    private final NameProvider nameProvider;

    private final Pattern regex;

    private final int skipPrefix;

    private final int skipSuffix;

    public SplitGroupProvider ( final NameProvider nameProvider, final String regex )
    {
        this ( nameProvider, Pattern.compile ( regex ), 0, 0 );
    }

    public SplitGroupProvider ( final NameProvider nameProvider, final Pattern pattern, final int skipPrefix, final int skipSuffix )
    {
        this.nameProvider = nameProvider;
        this.regex = pattern;
        this.skipPrefix = skipPrefix;
        this.skipSuffix = skipSuffix;
    }

    public SplitGroupProvider ( final NameProvider nameProvider, final String regex, final int skipPrefix, final int skipSuffix )
    {
        this ( nameProvider, Pattern.compile ( regex ), skipPrefix, skipSuffix );
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
            final String[] tok = this.regex.split ( name );
            if ( this.skipPrefix + this.skipSuffix >= tok.length )
            {
                // no groups
                return new String[0];
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
            logger.warn ( "Failed to split", e );
            return null;
        }
    }

}
