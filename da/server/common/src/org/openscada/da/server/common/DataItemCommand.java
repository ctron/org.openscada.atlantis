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

package org.openscada.da.server.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.core.WriteAttributeResults;

public class DataItemCommand extends DataItemOutput
{

    private static Logger logger = Logger.getLogger ( DataItemCommand.class );

    /**
     * The listener interface
     * @author Jens Reimann
     *
     */
    public static interface Listener
    {
        public void command ( Variant value ) throws Exception;
    }

    public DataItemCommand ( final String name )
    {
        super ( name );
    }

    private final List<Listener> listeners = new CopyOnWriteArrayList<Listener> ();

    public void writeValue ( final Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        for ( final Listener listener : this.listeners )
        {
            try
            {
                listener.command ( value );
            }
            catch ( final Throwable e )
            {
                logger.warn ( "Failed to run listener", e );
                throw new InvalidOperationException ();
            }
        }
    }

    /**
     * Add a new listener which gets called on write requests
     * @param listener listener to add
     */
    public void addListener ( final Listener listener )
    {
        this.listeners.add ( listener );
    }

    /**
     * Remove a listener from the list
     * @param listener listener to remove
     */
    public void removeListener ( final Listener listener )
    {
        this.listeners.remove ( listener );
    }

    public Map<String, Variant> getAttributes ()
    {
        return new HashMap<String, Variant> ();
    }

    public WriteAttributeResults setAttributes ( final Map<String, Variant> attributes )
    {
        return WriteAttributesHelper.errorUnhandled ( null, attributes );
    }

}
