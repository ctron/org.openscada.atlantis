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

package org.openscada.da.server.common.chain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    protected Map<String,Variant> _primaryAttributes = null;
    protected AttributeManager _secondaryAttributes = null;
    protected List<ChainProcessEntry> _chain = new LinkedList<ChainProcessEntry> ();

    public DataItemBaseChained ( DataItemInformation di )
    {
        super ( di );

        _primaryAttributes = new HashMap<String, Variant> ();
        _secondaryAttributes = new AttributeManager ( this );
    }

    public synchronized void updateAttributes ( Map<String, Variant> attributes )
    {
        Map<String, Variant> diff = new HashMap <String, Variant> ();
        AttributesHelper.mergeAttributes ( _primaryAttributes, attributes, diff );
        
        if ( diff.size () > 0 )
        {
            process ();
        }
    }

    public Map<String, Variant> getAttributes ()
    {
        return _secondaryAttributes.get ();
    }
  
    public synchronized WriteAttributeResults setAttributes ( Map<String, Variant> attributes )
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
    
    public synchronized void addChainElement ( EnumSet<IODirection> when, ChainItem item )
    {
        if ( _chain.add ( new ChainProcessEntry ( when, item ) ) )
        {
            process ();
        }
    }
    
    public synchronized void addChainElement ( IODirection when, ChainItem item )
    {
        if ( _chain.add ( new ChainProcessEntry ( EnumSet.of ( when ), item ) ) )
        {
            process ();
        }
    }

    public synchronized void removeChainElement ( EnumSet<IODirection> when, ChainItem item )
    {
        int n = 0;
        
        for ( Iterator<ChainProcessEntry> i = _chain.iterator ();  i.hasNext ();  )
        {
            ChainProcessEntry entry = i.next ();

            if ( entry.getWhen ().equals ( when ) )
                if ( entry.getWhat () == item )
                {
                    i.remove ();
                    n++;
                }
        }
        
        if ( n > 0 )
            process ();
    }

    protected synchronized Collection<ChainProcessEntry> getChainCopy ()
    {
        return new ArrayList<ChainProcessEntry> ( _chain );
    }


}