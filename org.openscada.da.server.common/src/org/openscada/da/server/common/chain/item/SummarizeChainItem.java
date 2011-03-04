/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.common.chain.item;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscada.core.Variant;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.chain.BaseChainItemCommon;
import org.openscada.da.server.common.chain.StringBinder;
import org.openscada.utils.str.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A chain item that sums up attribute entries that match a specific condition.
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 *
 */
public abstract class SummarizeChainItem extends BaseChainItemCommon
{

    private final static Logger logger = LoggerFactory.getLogger ( SummarizeChainItem.class );

    private final String sumStateName;

    private final String sumCountName;

    private final String sumListName;

    private final String sumIgnoreName;

    private final StringBinder ignoreBinder;

    public SummarizeChainItem ( final HiveServiceRegistry serviceRegistry, final String baseName )
    {
        super ( serviceRegistry );

        this.sumStateName = baseName;
        this.sumCountName = baseName + ".count";
        this.sumListName = baseName + ".items";
        this.sumIgnoreName = baseName + ".ignore";

        setReservedAttributes ( this.sumStateName, this.sumCountName, this.sumListName );

        this.ignoreBinder = new StringBinder ();
        addBinder ( this.sumIgnoreName, this.ignoreBinder );
    }

    /**
     * The method that will check if the attribute entry matches the condition.
     * @param value The current item value
     * @param attributeName The attribute name
     * @param attributeValue The attribute value
     * @return <code>true</code> if the entry should match, <code>false</code> otherwise
     */
    protected abstract boolean matches ( Variant value, String attributeName, Variant attributeValue );

    @Override
    public Variant process ( final Variant value, final Map<String, Variant> attributes )
    {
        attributes.put ( this.sumStateName, null );
        attributes.put ( this.sumCountName, null );
        attributes.put ( this.sumListName, null );

        long count = 0;
        final List<String> items = new LinkedList<String> ();
        final Set<String> ignoreItems = getIgnoreItems ();

        for ( final Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            final String attributeName = entry.getKey ();

            // ignore our own entries
            if ( !attributeName.equals ( this.sumStateName ) && !attributeName.equals ( this.sumCountName ) && !attributeName.equals ( this.sumListName ) && !ignoreItems.contains ( attributeName ) )
            {
                try
                {
                    if ( matches ( value, attributeName, entry.getValue () ) )
                    {
                        if ( entry.getValue () != null && entry.getValue ().asBoolean () )
                        {
                            count++;
                            items.add ( entry.getKey () );
                        }
                    }
                }
                catch ( final Exception e )
                {
                    logger.warn ( String.format ( "Failed to summarize item '%s'", attributeName ), e );
                }
            }
        }

        attributes.put ( this.sumStateName, Variant.valueOf ( count > 0 ) );
        attributes.put ( this.sumCountName, Variant.valueOf ( count ) );
        attributes.put ( this.sumListName, Variant.valueOf ( StringHelper.join ( items, ", " ) ) );

        addAttributes ( attributes );

        // no change
        return null;
    }

    protected Set<String> getIgnoreItems ()
    {
        final String txt = this.ignoreBinder.getValue ();
        if ( txt != null )
        {
            return new HashSet<String> ( Arrays.asList ( txt.split ( ",\\s" ) ) );
        }
        return Collections.emptySet ();
    }
}
