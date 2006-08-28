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

package org.openscada.da.core.common.chain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.da.core.WriteAttributesOperationListener.Result;
import org.openscada.da.core.WriteAttributesOperationListener.Results;
import org.openscada.da.core.data.Variant;

public abstract class BaseChainItemCommon implements ChainItem
{

    private Set<String> _reservedAttributes = new HashSet<String> ();
    private Map<String, AttributeBinder> _binders = new HashMap<String, AttributeBinder> ();
    
    public Results setAttributes ( Map<String, Variant> attributes )
    {
        Results results = new Results ();
        
        for ( Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            if ( _reservedAttributes.contains ( entry.getKey() ) )
            {
                results.put ( entry.getKey (), new Result ( new Exception ( "Attribute may not be set" ) ) );
            }
            else if ( _binders.containsKey ( entry.getKey () ) )
            {
                try
                {
                    _binders.get ( entry.getKey () ).bind ( entry.getValue () );
                    results.put ( entry.getKey (), new Result () );
                }
                catch ( Exception e )
                {
                   results.put ( entry.getKey (), new Result ( e ) );
                }
            }
        }
        
        return results;
    }
    
    public void setReservedAttributes ( String...reservedAttributes )
    {
        _reservedAttributes.addAll ( Arrays.asList ( reservedAttributes ) );
    }
    
    public void addBinder ( String name, AttributeBinder binder )
    {
        _binders.put ( name, binder );
    }
    
    public void removeBinder ( String name )
    {
        _binders.remove ( name );
    }
    
    public void addAttributes ( Map<String, Variant> attributes )
    {
        for ( Map.Entry<String, AttributeBinder> entry : _binders.entrySet () )
        {
            attributes.put ( entry.getKey (), entry.getValue ().getAttributeValue () );
        }
    }

}
