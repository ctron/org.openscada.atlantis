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

package org.openscada.da.server.common;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.server.DataItemInformation;

/**
 * This is an abstract base class for the {@link DataItem} interface. It also supports
 * the {@link SuspendableDataItem} interface.
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 * @see SuspendableDataItem
 */
public abstract class DataItemBase implements DataItem
{
    protected ItemListener _listener;

    private DataItemInformation _information;

    public DataItemBase ( DataItemInformation information )
    {
        _information = information;
    }

    public DataItemInformation getInformation ()
    {
        return _information;
    }

    public void setListener ( ItemListener listener )
    {
        synchronized ( this )
        {
            if ( _listener != listener )
            {
                handleListenerChange ( listener );
            }
        }
    }

    protected void handleListenerChange ( ItemListener listener )
    {
        if ( listener == null )
        {
            if ( this instanceof SuspendableDataItem )
            {
                ( (SuspendableDataItem)this ).suspend ();
            }
        }
        else if ( _listener == null )
        {
            if ( this instanceof SuspendableDataItem )
            {
                ( (SuspendableDataItem)this ).wakeup ();
            }
        }
        _listener = listener;
    }

    public void notifyValue ( Variant value )
    {
        synchronized ( this )
        {
            if ( _listener != null )
            {
                _listener.valueChanged ( this, value );
            }
        }
    }

    /**
     * Notify internal listeners ( most commonly the hive ) about
     * changes in the attribute set.
     * <p>
     * If the change set is empty the event will not be forwarded
     * @param attributes the list of changes made to the attributes
     */
    public void notifyAttributes ( Map<String, Variant> attributes )
    {
        if ( attributes.size () <= 0 )
        {
            return;
        }

        synchronized ( this )
        {
            if ( _listener != null )
            {
                _listener.attributesChanged ( this, attributes );
            }
        }

    }

}
