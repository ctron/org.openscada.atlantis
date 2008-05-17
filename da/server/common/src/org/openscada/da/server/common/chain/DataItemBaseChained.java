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

package org.openscada.da.server.common.chain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.openscada.core.Variant;
import org.openscada.core.utils.AttributesHelper;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.server.DataItemInformation;
import org.openscada.da.server.common.AttributeManager;
import org.openscada.da.server.common.DataItemBase;
import org.openscada.da.server.common.WriteAttributesHelper;

public abstract class DataItemBaseChained extends DataItemBase
{
    protected Map<String, Variant> _primaryAttributes = null;
    protected AttributeManager _secondaryAttributes = null;

    /**
     * The chain if items used for calculation
     */
    protected Set<ChainProcessEntry> _chain = new CopyOnWriteArraySet<ChainProcessEntry> ();

    public DataItemBaseChained ( DataItemInformation dataItemInformation )
    {
        super ( dataItemInformation );

        _primaryAttributes = new HashMap<String, Variant> ();
        _secondaryAttributes = new AttributeManager ( this );
    }

    public void updateAttributes ( Map<String, Variant> attributes )
    {
        synchronized ( _primaryAttributes )
        {
            Map<String, Variant> diff = new HashMap<String, Variant> ();
            AttributesHelper.mergeAttributes ( _primaryAttributes, attributes, diff );

            if ( diff.size () > 0 )
            {
                process ();
            }
        }
    }
    
    /**
     * Remove all attributes
     *
     */
    public void clearAttributes ()
    {
        synchronized ( _primaryAttributes )
        {
            if ( _primaryAttributes.size () > 0 )
            {
                _primaryAttributes.clear ();
                process ();
            }
        }
    }

    public Map<String, Variant> getAttributes ()
    {
        return _secondaryAttributes.get ();
    }

    public WriteAttributeResults setAttributes ( Map<String, Variant> attributes )
    {
        WriteAttributeResults writeAttributeResults = new WriteAttributeResults ();

        for ( ChainProcessEntry chainEntry : getChainCopy () )
        {
            ChainItem item = chainEntry.getWhat ();

            WriteAttributeResults partialResult = item.setAttributes ( attributes );
            if ( partialResult != null )
            {
                for ( Map.Entry<String, WriteAttributeResult> entry : partialResult.entrySet () )
                {
                    if ( entry.getValue ().isError () )
                    {
                        attributes.remove ( entry.getKey () );
                    }
                    writeAttributeResults.put ( entry.getKey (), entry.getValue () );
                }
            }
        }

        process ();

        return WriteAttributesHelper.errorUnhandled ( writeAttributeResults, attributes );
    }

    protected abstract void process ();

    /**
     * Replace the current chain with the new one
     * @param chain the new chain
     */
    public void setChain ( Collection<ChainProcessEntry> chain )
    {
        if ( chain == null )
        {
            _chain = new CopyOnWriteArraySet<ChainProcessEntry> ();
        }
        else
        {
            Set<ChainProcessEntry> newChain = new CopyOnWriteArraySet<ChainProcessEntry> ( chain );
            for ( ChainProcessEntry entry : newChain )
            {
                entry.getWhat ().dataItemChanged ( this );
            }
            _chain = newChain;
        }
        process ();
    }

    public void addChainElement ( EnumSet<IODirection> when, ChainItem item )
    {
        if ( _chain.add ( new ChainProcessEntry ( when, item ) ) )
        {
            item.dataItemChanged ( this );
            process ();
        }
    }

    public void addChainElement ( IODirection when, ChainItem item )
    {
        if ( _chain.add ( new ChainProcessEntry ( EnumSet.of ( when ), item ) ) )
        {
            item.dataItemChanged ( this );
            process ();
        }
    }

    public void removeChainElement ( EnumSet<IODirection> when, ChainItem item )
    {
        int n = 0;

        for ( Iterator<ChainProcessEntry> i = _chain.iterator (); i.hasNext (); )
        {
            ChainProcessEntry entry = i.next ();

            if ( entry.getWhen ().equals ( when ) )
            {
                if ( entry.getWhat () == item )
                {
                    i.remove ();
                    n++;
                }
            }
        }

        if ( n > 0 )
        {
            process ();
            item.dataItemChanged ( null );
        }
    }

    protected Collection<ChainProcessEntry> getChainCopy ()
    {
        return new ArrayList<ChainProcessEntry> ( _chain );
    }

}