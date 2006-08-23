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

package org.openscada.da.core.common.chained;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.common.AttributeManager;
import org.openscada.da.core.common.DataItemInformationBase;
import org.openscada.da.core.common.ItemListener;
import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;

public class DataItemInputChained extends DataItemBaseChained
{
    private Variant _primaryValue = new Variant ();
    private Variant _secondaryValue = new Variant ();
    
    private List<InputChainItem> _inputChain = new LinkedList<InputChainItem> ();
    
    public DataItemInputChained ( DataItemInformation di )
    {
        super ( di );
        
        _primaryAttributes = new HashMap<String, Variant> ();
        _secondaryAttributes = new AttributeManager ( this );
    }
    
    public DataItemInputChained ( String id )
    {
        this ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT ) ) );
    }
    
    synchronized public void addInputChainElement ( InputChainItem item )
    {
        if ( _inputChain.add ( item ) )
        {
            process ();
        }
    }

    synchronized public void removeInputChainElement ( InputChainItem item )
    {
        if ( _inputChain.remove ( item ) )
        {
            process ();
        }
    }
 
    synchronized public void updateValue ( Variant value )
    {
        if ( _primaryValue.equals ( value ) )
            return;
        
        _primaryValue = new Variant ( value );
        process ();
    }
    
    @Override
    protected void process ()
    {
        Variant primaryValue = new Variant ( _primaryValue );
        Map<String, Variant> primaryAttributes = new HashMap<String, Variant> ( _primaryAttributes );
        
        for ( InputChainItem item : _inputChain )
        {
            item.process ( primaryValue, primaryAttributes );
        }
        
        if ( !_secondaryValue.equals ( primaryValue ) )
        {
            _secondaryValue = new Variant ( primaryValue );
            notifyValue ( _secondaryValue );
        }
        _secondaryAttributes.set ( primaryAttributes );
    }

    public Variant getValue () throws InvalidOperationException
    {
        return new Variant ( _secondaryValue );
    }

    public void setValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        throw new InvalidOperationException ();
    }
    
    @Override
    public void setListener ( ItemListener listener )
    {
        super.setListener ( listener );
        if ( listener != null )
        {
            if ( !_secondaryValue.isNull () )
                notifyValue ( _secondaryValue );
            if ( _secondaryAttributes.get ().size () > 0 )
                notifyAttributes ( _secondaryAttributes.get () );
        }
    }

    @Override
    synchronized protected Collection<BaseChainItem> getChainItems ()
    {
        return Arrays.asList ( _inputChain.toArray ( new BaseChainItem[0] ) );
    }
    
}
