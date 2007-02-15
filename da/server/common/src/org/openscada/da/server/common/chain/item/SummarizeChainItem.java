/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.BaseChainItemCommon;
import org.openscada.utils.str.StringHelper;

/**
 * A chain item that sums up attribute entries that match a specific condition.
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public abstract class SummarizeChainItem extends BaseChainItemCommon
{
    private String _sumStateName;
    private String _sumCountName;
    private String _sumListName;

    public SummarizeChainItem ( String baseName )
    {
        super ();
        
        _sumStateName = baseName;
        _sumCountName = baseName + ".count";
        _sumListName = baseName + ".items";

        setReservedAttributes ( _sumStateName, _sumCountName, _sumListName );
    }
    
    /**
     * The method that will check if the attribute entry matches the condition.
     * @param value The current item value
     * @param attributeName The attribute name
     * @param attributeValue The attribute value
     * @return <code>true</code> if the entry should match, <code>false</code> otherwise
     */
    protected abstract boolean matches ( Variant value, String attributeName, Variant attributeValue );

    public void process ( Variant value, Map<String, Variant> attributes )
    {
        attributes.put ( _sumStateName, null );
        attributes.put ( _sumCountName, null );
        attributes.put ( _sumListName, null );

        long count = 0;
        List<String> items = new LinkedList<String> ();

        for ( Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            if ( matches ( value, entry.getKey (), entry.getValue () ) )
            {
                if ( entry.getValue ().asBoolean () )
                {
                    count++;
                    items.add ( entry.getKey () );
                }
            }
        }

        attributes.put ( _sumStateName, new Variant ( count > 0 ) );
        attributes.put ( _sumCountName, new Variant ( count ) );
        attributes.put ( _sumListName, new Variant ( StringHelper.join ( items, ", " ) ) );

        addAttributes ( attributes );
    }
}
