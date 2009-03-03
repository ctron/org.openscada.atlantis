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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.SuspendableDataItem;

public abstract class DataItemOutputChained extends DataItemBaseChained implements SuspendableDataItem
{

    public DataItemOutputChained ( final DataItemInformation dataItemInformation )
    {
        super ( dataItemInformation );
    }

    public DataItemOutputChained ( final String id )
    {
        this ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.OUTPUT ) ) );
    }

    public Variant readValue () throws InvalidOperationException
    {
        throw new InvalidOperationException ();
    }

    synchronized public void writeValue ( final Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        process ( value );

        writeCalculatedValue ( value );
    }

    @Override
    protected void process ()
    {
        process ( null );
    }

    protected void process ( final Variant value )
    {
        final Map<String, Variant> primaryAttributes = new HashMap<String, Variant> ( this._primaryAttributes );

        for ( final ChainProcessEntry entry : getChainCopy () )
        {
            if ( entry.getWhen ().contains ( IODirection.OUTPUT ) )
            {
                entry.getWhat ().process ( value, primaryAttributes );
            }
        }

        this._secondaryAttributes.set ( null, primaryAttributes );
    }

    public void suspend ()
    {
    }

    public void wakeup ()
    {
        if ( this._secondaryAttributes.get ().size () > 0 )
        {
            notifyData ( null, this._secondaryAttributes.get () );
        }
    }

    protected abstract void writeCalculatedValue ( Variant value ) throws NullValueException, NotConvertableException;

}
