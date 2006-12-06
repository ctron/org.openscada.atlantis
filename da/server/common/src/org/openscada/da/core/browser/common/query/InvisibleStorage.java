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

package org.openscada.da.core.browser.common.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class InvisibleStorage implements SubscribeableStorage
{
    private Set<ItemDescriptor> _items = new HashSet<ItemDescriptor> ();
    private Collection<ItemStorage> _childs = new LinkedList <ItemStorage> ();
    
    public void added ( ItemDescriptor descriptor )
    {
        synchronized ( this )
        {
            if ( _items.contains ( descriptor ) )
                return;

            _items.add ( descriptor );
            notifyAdd ( descriptor );
        }
    }

    public void removed ( ItemDescriptor descriptor )
    {
        synchronized ( this )
        {
            if ( !_items.contains ( descriptor ) )
                return;

            _items.remove ( descriptor );
            notifyRemove ( descriptor );
        }
    }
    
    public void addChild ( ItemStorage child )
    {
        synchronized ( this )
        {
            _childs.add ( child );

            // now push all possible descriptors
            for ( ItemDescriptor desc : _items )
            {
                child.added ( desc );
            }
        }
    }
    
    public void removeChild ( ItemStorage child )
    {
        synchronized ( this )
        {
            _childs.remove ( child );
        }
    }
    
    private void notifyAdd ( ItemDescriptor desc )
    {
        // notify childs
        for ( ItemStorage child : _childs )
        {
            child.added ( desc );
        }
    }
    
    private void notifyRemove ( ItemDescriptor desc )
    {
        // notify childs
        for ( ItemStorage child : _childs )
        {
            child.removed ( desc );
        }
    }

}
