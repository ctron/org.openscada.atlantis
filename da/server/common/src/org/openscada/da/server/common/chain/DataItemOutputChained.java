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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.server.DataItemInformation;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.ItemListener;

public abstract class DataItemOutputChained extends DataItemBaseChained
{

    public DataItemOutputChained ( DataItemInformation dataItemInformation )
    {
        super ( dataItemInformation );
    }
    
    public DataItemOutputChained ( String id )
    {
        this ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.OUTPUT ) ) );
    }

    public Variant getValue () throws InvalidOperationException
    {
        throw new InvalidOperationException ();
    }

    synchronized public void setValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        process ( value );
        
        writeValue ( value );
    }

    @Override
    protected void process ()
    {
        process ( null );
    }
    
    protected void process ( Variant value )
    {
        Map<String, Variant> primaryAttributes = new HashMap<String, Variant> ( _primaryAttributes );
        
        for ( ChainProcessEntry entry: _chain )
        {
            if ( entry.getWhen ().contains ( IODirection.OUTPUT ) )
            {
                entry.getWhat ().process ( value, primaryAttributes );
            }
        }
        
        _secondaryAttributes.set ( primaryAttributes );
    }
    
    @Override
    public void setListener ( ItemListener listener )
    {
        super.setListener ( listener );
        
        if ( listener != null )
        {
            if ( _secondaryAttributes.get ().size () > 0 )
            {
                notifyAttributes ( _secondaryAttributes.get () );
            }
        }
    }
    
    protected abstract void writeValue ( Variant value ) throws NullValueException, NotConvertableException;

}
