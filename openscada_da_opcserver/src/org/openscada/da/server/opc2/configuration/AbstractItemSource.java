/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.opc2.configuration;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;

public abstract class AbstractItemSource implements ItemSource
{
    private static Logger logger = Logger.getLogger ( AbstractItemSource.class );

    private final Set<ItemSourceListener> listeners = new CopyOnWriteArraySet<ItemSourceListener> ();

    public void addListener ( final ItemSourceListener listener )
    {
        this.listeners.add ( listener );
    }

    public void removeListener ( final ItemSourceListener listener )
    {
        this.listeners.remove ( listener );
    }

    protected void fireAvailableItemsChanged ( final Set<ItemDescription> items )
    {
        for ( final ItemSourceListener listener : this.listeners )
        {
            try
            {
                listener.availableItemsChanged ( items );
            }
            catch ( final Throwable e )
            {
                logger.info ( "Failed to handle availableItemsChanged", e );
            }
        }
    }

    public abstract void activate ();

    public void deactivate ()
    {
        this.listeners.clear ();
    }

}
