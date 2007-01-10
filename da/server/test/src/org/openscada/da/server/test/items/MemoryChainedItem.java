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
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.common.DataItemInformationBase;
import org.openscada.da.core.common.chain.AttributeBinder;
import org.openscada.da.core.common.chain.BaseChainItemCommon;
import org.openscada.da.core.common.chain.ChainItem;
import org.openscada.da.core.common.chain.ChainProcessEntry;
import org.openscada.da.core.common.chain.MemoryItemChained;

public class MemoryChainedItem extends MemoryItemChained
{
    
    private class AddClassAttributeBinder implements AttributeBinder
    {
        private MemoryChainedItem _item = null;
        private IODirection _direction = null;
        
        public AddClassAttributeBinder ( MemoryChainedItem item, IODirection direction )
        {
            super ();
            _item = item;
            _direction = direction;
        }
        
        public void bind ( Variant value ) throws Exception
        {
           if ( value != null )
               if ( !value.isNull () )
                   _item.addChainElement ( _direction, value.asString () );
        }

        public Variant getAttributeValue ()
        {
            return null;
        }
        
    }
    
    private class RemoveClassAttributeBinder implements AttributeBinder
    {
        private MemoryChainedItem _item = null;
        private IODirection _direction = null;
        
        public RemoveClassAttributeBinder ( MemoryChainedItem item, IODirection direction )
        {
            super ();
            _item = item;
            _direction = direction;
        }
        
        public void bind ( Variant value ) throws Exception
        {
            if ( value != null )
                if ( !value.isNull () )
                    _item.removeChainElement ( _direction, value.asString () );
        }

        public Variant getAttributeValue ()
        {
            return null;
        }
        
    }
    
    private class InjectChainItem extends BaseChainItemCommon
    {
        private MemoryChainedItem _item = null;
        
        public InjectChainItem ( MemoryChainedItem item )
        {
            _item = item;
            
            addBinder ( "org.openscada.da.test.chain.input.add", new AddClassAttributeBinder ( item, IODirection.INPUT ) );
            addBinder ( "org.openscada.da.test.chain.input.remove", new RemoveClassAttributeBinder ( item, IODirection.INPUT ) );
            addBinder ( "org.openscada.da.test.chain.outpt.add", new AddClassAttributeBinder ( item, IODirection.OUTPUT ) );
            addBinder ( "org.openscada.da.test.chain.output.remove", new RemoveClassAttributeBinder ( item, IODirection.OUTPUT ) );
            setReservedAttributes ( "org.openscada.da.test.chain.value" );
        }
        
        public void process ( Variant value, Map<String, Variant> attributes )
        {
            int i = 0;
            StringBuilder str = new StringBuilder ();
            for ( ChainProcessEntry item : _item.getChainEntries () )
            {
                if ( i > 0 )
                    str.append ( ", " );
                
                str.append ( item.getWhat ().getClass ().getCanonicalName () );
                str.append ( "(" );
                str.append ( item.getWhen ().toString () );
                str.append ( ")" );
                
                i++;
            }
            attributes.put ( "org.openscada.da.test.chain.value", new Variant ( str.toString () ) );
        }
        
    }
    
    public MemoryChainedItem ( String id )
    {
        super ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) ) );
        addChainElement ( IODirection.INPUT, new InjectChainItem ( this ) );
    }
   
    public void addChainElement ( IODirection direction, String className ) throws Exception
    {
        Class itemClass = Class.forName ( className );
        Object o = itemClass.newInstance ();

        addChainElement ( direction, (ChainItem )o );
    }
    
    synchronized public void removeChainElement ( IODirection direction, String className ) throws Exception
    {
        for ( ChainProcessEntry entry : getChainEntries () )
        {
            if ( entry.getWhat ().getClass ().getCanonicalName ().equals ( className ) )
            {
                if ( entry.getWhen ().equals ( EnumSet.of ( direction ) ) )
                    removeChainElement ( entry.getWhen (), entry.getWhat () );
                return;
            }
        }
        throw new Exception ( "Item not found" );
    }

}
