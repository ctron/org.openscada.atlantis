/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.test.items;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.da.core.IODirection;
import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.common.DataItemInformationBase;
import org.openscada.da.core.common.chained.AttributeBinder;
import org.openscada.da.core.common.chained.DataItemInputChained;
import org.openscada.da.core.common.chained.InputChainItem;
import org.openscada.da.core.common.chained.InputChainItemCommon;
import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;

public class MemoryChainedInputItem extends DataItemInputChained
{
    
    private class AddClassAttributeBinder implements AttributeBinder
    {
        private MemoryChainedInputItem _item = null;
        
        public AddClassAttributeBinder ( MemoryChainedInputItem item )
        {
            super ();
            _item = item;
        }
        
        public void bind ( Variant value ) throws Exception
        {
           if ( value != null )
               if ( !value.isNull () )
                   _item.addInputChainElement ( value.asString () );
        }

        public Variant getAttributeValue ()
        {
            return null;
        }
        
    }
    
    private class RemoveClassAttributeBinder implements AttributeBinder
    {
        private MemoryChainedInputItem _item = null;
        
        public RemoveClassAttributeBinder ( MemoryChainedInputItem item )
        {
            super ();
            _item = item;
        }
        
        public void bind ( Variant value ) throws Exception
        {
            if ( value != null )
                if ( !value.isNull () )
                    _item.removeInputChainElement ( value.asString () );
        }

        public Variant getAttributeValue ()
        {
            return null;
        }
        
    }
    
    private class InjectInputChainItem extends InputChainItemCommon
    {
        private MemoryChainedInputItem _item = null;
        
        public InjectInputChainItem ( MemoryChainedInputItem item )
        {
            _item = item;
            
            addBinder ( "org.openscada.da.test.inputchain.add", new AddClassAttributeBinder ( item ) );
            addBinder ( "org.openscada.da.test.inputchain.remove", new RemoveClassAttributeBinder ( item ) );
            setReservedAttributes ( "org.openscada.da.test.inputchain.value" );
        }
        
        public void process ( Variant value, Map<String, Variant> attributes )
        {
            int i = 0;
            StringBuilder str = new StringBuilder ();
            for ( InputChainItem item : _item.getInputChainItems () )
            {
                if ( i > 0 )
                    str.append ( ", " );
                str.append ( item.getClass ().getCanonicalName () );
                i++;
            }
            attributes.put ( "org.openscada.da.test.inputchain.value", new Variant ( str.toString () ) );
        }
        
    }
    
    private List<InputChainItem> _inputChainItems = new LinkedList<InputChainItem> ();

    public MemoryChainedInputItem ( String id )
    {
        super ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) ) );
        addInputChainElement ( new InjectInputChainItem ( this ) );
    }
    
    @Override
    public void setValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        updateValue ( value );
    }
    
    public void addInputChainElement ( String className ) throws Exception
    {
        Class itemClass = Class.forName ( className );
        Object o = itemClass.newInstance ();
        if ( _inputChainItems.add ( (InputChainItem)o ) )
        {
            addInputChainElement ( (InputChainItem )o );
        }
    }
    
    public void removeInputChainElement ( String className ) throws Exception
    {
        for ( InputChainItem item : _inputChainItems )
        {
            if ( item.getClass ().getCanonicalName ().equals ( className ) )
            {
                removeInputChainElement ( item );
                _inputChainItems.remove ( item );
                return;
            }
        }
        throw new Exception ( "Item not found" );
    }
    
    public List<InputChainItem> getInputChainItems ()
    {
        return _inputChainItems;
    }

}
