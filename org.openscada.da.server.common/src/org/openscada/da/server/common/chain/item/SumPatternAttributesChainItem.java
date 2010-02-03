/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.chain.item;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.server.common.HiveServiceRegistry;

/**
 * A chain item that summarizes based on a pattern. 
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public class SumPatternAttributesChainItem extends SummarizeChainItem
{
    @SuppressWarnings ( "unused" )
    private static Logger _log = Logger.getLogger ( SumPatternAttributesChainItem.class );

    private final Pattern _pattern;

    public SumPatternAttributesChainItem ( final HiveServiceRegistry serviceRegistry, final String baseName, final String pattern )
    {
        this ( serviceRegistry, baseName, Pattern.compile ( pattern ) );
    }

    public SumPatternAttributesChainItem ( final HiveServiceRegistry serviceRegistry, final String baseName, final Pattern pattern )
    {
        super ( serviceRegistry, baseName );

        this._pattern = pattern;
    }

    @Override
    protected boolean matches ( final Variant value, final String attributeName, final Variant attributeValue )
    {
        return this._pattern.matcher ( attributeName ).matches ();
    }

}
