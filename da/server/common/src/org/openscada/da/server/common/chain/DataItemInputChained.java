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

public class DataItemInputChained extends DataItemBaseChained
{
    protected Variant _primaryValue = new Variant ();
    protected Variant _secondaryValue = new Variant ();
    
    protected boolean _filterNoChange = true;

    public DataItemInputChained ( DataItemInformation dataItemInformation )
    {
        super ( dataItemInformation );
    }

    public DataItemInputChained ( String id )
    {
        this ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT ) ) );
    }

    public synchronized void updateValue ( Variant value )
    {
        if ( _filterNoChange && _primaryValue.equals ( value ) )
        {
            return;
        }

        _primaryValue = new Variant ( value );
        process ();
    }

    @Override
    protected void process ()
    {
        Variant newSecondaryValue = new Variant ( _primaryValue );
        Map<String, Variant> primaryAttributes = new HashMap<String, Variant> ( _primaryAttributes );

        for ( ChainProcessEntry entry : getChainCopy () )
        {
            if ( entry.getWhen ().contains ( IODirection.INPUT ) )
            {
                entry.getWhat ().process ( newSecondaryValue, primaryAttributes );
            }
        }

        if ( (!_filterNoChange) || (!_secondaryValue.equals ( newSecondaryValue )) )
        {
            _secondaryValue = new Variant ( newSecondaryValue );
            notifyValue ( _secondaryValue );
        }
        _secondaryAttributes.set ( primaryAttributes );
    }

    public Variant readValue () throws InvalidOperationException
    {
        return new Variant ( _secondaryValue );
    }

    public void writeValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        throw new InvalidOperationException ();
    }

    @Override
    protected Map<String, Variant> getCacheAttributes ()
    {
        return _secondaryAttributes.get ();
    }
    
    @Override
    protected Variant getCacheValue ()
    {
        return _secondaryValue;
    }
    
    public boolean isFilterNoChange ()
    {
        return _filterNoChange;
    }

    /**
     * Set the state of the "no change" filter.
     * 
     * <br/>
     * If the noChange filter it set to <code>true</code> (the default) update calls
     * with the value already set will be ignored and do not generate a value event.
     * If the noChange filter is set to <code>false</code> then each update call, also
     * if the value did not change, will generate update events. 
     * @param filterNoChange new state of the nochange filter
     */
    public void setFilterNoChange ( boolean filterNoChange )
    {
        _filterNoChange = filterNoChange;
    }

}
