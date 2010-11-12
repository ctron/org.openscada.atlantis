/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.browser.common.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class InvisibleStorage implements SubscribeableStorage
{
    private final Set<ItemDescriptor> items = new HashSet<ItemDescriptor> ();

    private final Collection<ItemStorage> childs = new LinkedList<ItemStorage> ();

    public void added ( final ItemDescriptor descriptor )
    {
        synchronized ( this )
        {
            if ( this.items.contains ( descriptor ) )
            {
                return;
            }

            this.items.add ( descriptor );
            notifyAdd ( descriptor );
        }
    }

    public void removed ( final ItemDescriptor descriptor )
    {
        synchronized ( this )
        {
            if ( !this.items.remove ( descriptor ) )
            {
                return;
            }

            notifyRemove ( descriptor );
        }
    }

    public void addChild ( final ItemStorage child )
    {
        synchronized ( this )
        {
            this.childs.add ( child );

            // now push all possible descriptors
            for ( final ItemDescriptor desc : this.items )
            {
                child.added ( desc );
            }
        }
    }

    public void removeChild ( final ItemStorage child )
    {
        synchronized ( this )
        {
            this.childs.remove ( child );
        }
    }

    private void notifyAdd ( final ItemDescriptor desc )
    {
        // notify childs
        for ( final ItemStorage child : this.childs )
        {
            child.added ( desc );
        }
    }

    private void notifyRemove ( final ItemDescriptor desc )
    {
        // notify childs
        for ( final ItemStorage child : this.childs )
        {
            child.removed ( desc );
        }
    }

    public void clear ()
    {
        synchronized ( this )
        {
            for ( final ItemDescriptor desc : this.items )
            {
                notifyRemove ( desc );
            }
            this.items.clear ();
        }
    }

}
