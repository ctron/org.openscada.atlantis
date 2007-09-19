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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.BaseChainItemCommon;
import org.openscada.da.server.common.chain.StringBinder;
import org.openscada.utils.str.StringHelper;

/**
 * A chain item that sums up attribute entries that match a specific condition.
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public abstract class SummarizeChainItem extends BaseChainItemCommon
{
    private static Logger _log = Logger.getLogger ( SummarizeChainItem.class );

    private String _sumStateName;
    private String _sumCountName;
    private String _sumListName;
    private String _sumIgnoreName;

    private StringBinder _ignoreBinder;

    public SummarizeChainItem ( String baseName )
    {
        super ();

        _sumStateName = baseName;
        _sumCountName = baseName + ".count";
        _sumListName = baseName + ".items";
        _sumIgnoreName = baseName + ".ignore";

        setReservedAttributes ( _sumStateName, _sumCountName, _sumListName );
        
        _ignoreBinder = new StringBinder ();
        addBinder ( _sumIgnoreName, _ignoreBinder );
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
        Set<String> ignoreItems = getIgnoreItems ();
        
        for ( Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            String attributeName = entry.getKey ();

            // ignore our own entries
            if ( !attributeName.equals ( _sumStateName ) && !attributeName.equals ( _sumCountName )
                    && !attributeName.equals ( _sumListName ) && !ignoreItems.contains ( attributeName ) )
            {
                try
                {
                    if ( matches ( value, attributeName, entry.getValue () ) )
                    {
                        if ( ( entry.getValue () != null ) && entry.getValue ().asBoolean () )
                        {
                            count++;
                            items.add ( entry.getKey () );
                        }
                    }
                }
                catch ( Exception e )
                {
                    _log.warn ( String.format ( "Failed to summarize item '%s'", attributeName ), e );
                }
            }
        }

        attributes.put ( _sumStateName, new Variant ( count > 0 ) );
        attributes.put ( _sumCountName, new Variant ( count ) );
        attributes.put ( _sumListName, new Variant ( StringHelper.join ( items, ", " ) ) );

        addAttributes ( attributes );
    }
    
    protected Set<String> getIgnoreItems ()
    {
        String txt = _ignoreBinder.getValue ();
        if ( txt != null )
        {
            return new HashSet<String> ( Arrays.asList ( txt.split ( ",\\s" ) ) );
        }
        return Collections.emptySet ();
    }
}
