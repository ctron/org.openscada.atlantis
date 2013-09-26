/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.eclipse.scada.da.server.modbus.driver;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.server.modbus.ModbusConstants;
import org.eclipse.scada.da.server.modbus.ModbusRegisterType;
import org.eclipse.scada.da.server.modbus.ModbusType;

public class SlaveDevice
{
    public static class Tag implements Comparable<Tag>, Serializable
    {
        private static final long serialVersionUID = -6386366933024223032L;

        private final String name;

        private final int address;

        private final ModbusType type;

        private final int priority;

        public Tag ( final String name, final int address, final ModbusType type, final int priority )
        {
            this.name = name;
            this.address = address;
            this.type = type;
            this.priority = priority;
        }

        public String getName ()
        {
            return this.name;
        }

        public int getAddress ()
        {
            return this.address;
        }

        public ModbusType getType ()
        {
            return this.type;
        }

        public int getPriority ()
        {
            return this.priority;
        }

        @Override
        public int compareTo ( final Tag o )
        {
            if ( this.address == o.address )
            {
                return 0;
            }
            return this.address < o.address ? -1 : +1;
        }

        @Override
        public int hashCode ()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.address;
            result = prime * result + ( this.name == null ? 0 : this.name.hashCode () );
            result = prime * result + ( this.type == null ? 0 : this.type.hashCode () );
            return result;
        }

        @Override
        public boolean equals ( final Object obj )
        {
            if ( this == obj )
            {
                return true;
            }
            if ( obj == null )
            {
                return false;
            }
            if ( getClass () != obj.getClass () )
            {
                return false;
            }
            final Tag other = (Tag)obj;
            if ( this.address != other.address )
            {
                return false;
            }
            if ( this.name == null )
            {
                if ( other.name != null )
                {
                    return false;
                }
            }
            else if ( !this.name.equals ( other.name ) )
            {
                return false;
            }
            if ( this.type == null )
            {
                if ( other.type != null )
                {
                    return false;
                }
            }
            else if ( !this.type.equals ( other.type ) )
            {
                return false;
            }
            return true;
        }

        @Override
        public String toString ()
        {
            return "Tag [address=" + this.address + ", name=" + this.name + ", type=" + this.type + "]";
        }
    }

    public static class Block implements Serializable
    {
        private static final long serialVersionUID = -8869215862104149347L;

        private final ModbusRegisterType registerType;

        private final int startAddress;

        private final int offset;

        private final int quantity;

        private final int priority;

        public Block ( final ModbusRegisterType registerType, final int startAddress, final int offset, final int quantity, final int priority )
        {
            this.registerType = registerType;
            this.startAddress = startAddress;
            this.offset = offset;
            this.quantity = quantity;
            this.priority = priority;
        }

        public ModbusRegisterType getRegisterType ()
        {
            return this.registerType;
        }

        public int getStartAddress ()
        {
            return this.startAddress;
        }

        public int getOffset ()
        {
            return this.offset;
        }

        public int getQuantity ()
        {
            return this.quantity;
        }

        public int getPriority ()
        {
            return this.priority;
        }

        @Override
        public int hashCode ()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.quantity;
            result = prime * result + ( this.registerType == null ? 0 : this.registerType.hashCode () );
            result = prime * result + this.startAddress;
            return result;
        }

        @Override
        public boolean equals ( final Object obj )
        {
            if ( this == obj )
            {
                return true;
            }
            if ( obj == null )
            {
                return false;
            }
            if ( getClass () != obj.getClass () )
            {
                return false;
            }
            final Block other = (Block)obj;
            if ( this.registerType == null )
            {
                if ( other.registerType != null )
                {
                    return false;
                }
            }
            else if ( !this.registerType.equals ( other.registerType ) )
            {
                return false;
            }
            if ( this.startAddress != other.startAddress )
            {
                return false;
            }
            if ( this.quantity != other.quantity )
            {
                return false;
            }
            return true;
        }

        @Override
        public String toString ()
        {
            return "Block [quantity=" + this.quantity + ", registerType=" + this.registerType + ", startAddress=" + this.startAddress + "]";
        }
    }

    public static class ItemValue implements Serializable
    {
        private static final long serialVersionUID = 7236643586835888104L;

        private final String name;

        private final Variant value;

        public ItemValue ( final String name, final Variant value )
        {
            this.name = name;
            this.value = value;
        }

        public String getName ()
        {
            return this.name;
        }

        public Variant getValue ()
        {
            return this.value;
        }

        @Override
        public int hashCode ()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( this.name == null ? 0 : this.name.hashCode () );
            result = prime * result + ( this.value == null ? 0 : this.value.hashCode () );
            return result;
        }

        @Override
        public boolean equals ( final Object obj )
        {
            if ( this == obj )
            {
                return true;
            }
            if ( obj == null )
            {
                return false;
            }
            if ( getClass () != obj.getClass () )
            {
                return false;
            }
            final ItemValue other = (ItemValue)obj;
            if ( this.name == null )
            {
                if ( other.name != null )
                {
                    return false;
                }
            }
            else if ( !this.name.equals ( other.name ) )
            {
                return false;
            }
            if ( this.value == null )
            {
                if ( other.value != null )
                {
                    return false;
                }
            }
            else if ( !this.value.equals ( other.value ) )
            {
                return false;
            }
            return true;
        }

        @Override
        public String toString ()
        {
            return "ItemValue [name=" + this.name + ", value=" + this.value + "]";
        }
    }

    private final Map<ModbusRegisterType, Map<String, Tag>> tagsByName = new HashMap<ModbusRegisterType, Map<String, Tag>> ();

    private final Map<ModbusRegisterType, Map<Integer, Tag>> tagsByAddress = new HashMap<ModbusRegisterType, Map<Integer, Tag>> ();

    private final byte unitIdentifier;

    private final String name;

    private final int discreteInputOffset;

    private final int coilOffset;

    private final int inputRegisterOffset;

    private final int holdingRegisterOffset;

    // following fields are a representation of the internal state of the modbus device
    private final BitSet discreteInputs = new BitSet ( ModbusConstants.MAX_ADDRESS );

    private final BitSet coils = new BitSet ( ModbusConstants.MAX_ADDRESS );

    private final ByteBuffer inputRegisters = ByteBuffer.allocate ( 2 * ModbusConstants.MAX_ADDRESS );

    private final ByteBuffer holdingRegisters = ByteBuffer.allocate ( 2 * ModbusConstants.MAX_ADDRESS );

    public SlaveDevice ( final byte unitIdentifier, final String name, final int discreteInputOffset, final int coilOffset, final int inputRegisterOffset, final int holdingRegisterOffset )
    {
        this.unitIdentifier = unitIdentifier;
        this.name = name;
        this.discreteInputOffset = discreteInputOffset;
        this.coilOffset = coilOffset;
        this.inputRegisterOffset = inputRegisterOffset;
        this.holdingRegisterOffset = holdingRegisterOffset;
        this.tagsByName.put ( ModbusRegisterType.DiscreteInputs, new HashMap<String, Tag> () );
        this.tagsByName.put ( ModbusRegisterType.Coils, new HashMap<String, Tag> () );
        this.tagsByName.put ( ModbusRegisterType.InputRegisters, new HashMap<String, Tag> () );
        this.tagsByName.put ( ModbusRegisterType.HoldingRegisters, new HashMap<String, Tag> () );
        this.tagsByAddress.put ( ModbusRegisterType.DiscreteInputs, new HashMap<Integer, Tag> () );
        this.tagsByAddress.put ( ModbusRegisterType.Coils, new HashMap<Integer, Tag> () );
        this.tagsByAddress.put ( ModbusRegisterType.InputRegisters, new HashMap<Integer, Tag> () );
        this.tagsByAddress.put ( ModbusRegisterType.HoldingRegisters, new HashMap<Integer, Tag> () );
    }

    public byte getUnitIdentifier ()
    {
        return this.unitIdentifier;
    }

    public String getName ()
    {
        return this.name;
    }

    public int getDiscreteInputOffset ()
    {
        return this.discreteInputOffset;
    }

    public int getCoilOffset ()
    {
        return this.coilOffset;
    }

    public int getInputRegisterOffset ()
    {
        return this.inputRegisterOffset;
    }

    public int getHoldingRegisterOffset ()
    {
        return this.holdingRegisterOffset;
    }

    public BitSet getDiscreteInputs ()
    {
        return this.discreteInputs;
    }

    public BitSet getCoils ()
    {
        return this.coils;
    }

    public ByteBuffer getInputRegisters ()
    {
        return this.inputRegisters;
    }

    public ByteBuffer getHoldingRegisters ()
    {
        return this.holdingRegisters;
    }

    public void addTag ( final ModbusRegisterType registerType, final String name, final int address, final ModbusType type, final int priority )
    {
        if ( this.tagsByName.get ( registerType ).containsKey ( name ) )
        {
            throw new IllegalArgumentException ( "tag with name " + name + " already registered" );
        }
        if ( this.tagsByAddress.get ( registerType ).containsKey ( address ) )
        {
            throw new IllegalArgumentException ( "tag (" + name + ") with address " + String.format ( "0x%04d", address ) + " already registered" );
        }
        final Tag tag = new Tag ( name, address, type, priority );
        this.tagsByName.get ( registerType ).put ( name, tag );
        switch ( type )
        {
            case FLOAT64:
            case INT64:
                this.tagsByAddress.get ( registerType ).put ( address + 3, tag );
                this.tagsByAddress.get ( registerType ).put ( address + 2, tag );
                //$FALL-THROUGH$
            case FLOAT32:
            case INT32:
                this.tagsByAddress.get ( registerType ).put ( address + 1, tag );
                //$FALL-THROUGH$
            case INT16:
            case BOOLEAN:
                this.tagsByAddress.get ( registerType ).put ( address, tag );
                break;
        }
    }

    public Tag tagByName ( final ModbusRegisterType registerType, final String name )
    {
        return this.tagsByName.get ( registerType ).get ( name );
    }

    public Tag tagByAdress ( final ModbusRegisterType registerType, final int address )
    {
        return this.tagsByAddress.get ( registerType ).get ( address );
    }

    public Variant getValue ( final ModbusRegisterType registerType, final String name )
    {
        final Tag tag = tagByName ( registerType, name );
        return getValue ( registerType, tag );
    }

    public Variant getValue ( final ModbusRegisterType registerType, final Tag tag )
    {
        final int address = tag.getAddress ();
        switch ( registerType )
        {
            case DiscreteInputs:
                return Variant.valueOf ( this.discreteInputs.get ( address ) );
            case Coils:
                return Variant.valueOf ( this.coils.get ( address ) );
            case InputRegisters:
                return toValue ( this.inputRegisters, tag.getAddress (), tag.getType () );
            case HoldingRegisters:
                return toValue ( this.holdingRegisters, tag.getAddress (), tag.getType () );
        }
        throw new IllegalArgumentException ( "registerType unknown" );
    }

    private Variant toValue ( final ByteBuffer buffer, final int address, final ModbusType type )
    {
        final int oldPosition = buffer.position ();
        Object no = null;
        buffer.position ( address * 2 );
        switch ( type )
        {
            case INT16:
                no = buffer.getShort ();
                break;
            case INT32:
                no = buffer.getInt ();
                break;
            case INT64:
                no = buffer.getLong ();
                break;
            case FLOAT32:
                no = buffer.getFloat ();
                break;
            case FLOAT64:
                no = buffer.getDouble ();
                break;
            case BOOLEAN:
                no = buffer.getShort () != 0;
                break;
        }
        buffer.position ( oldPosition );
        return Variant.valueOf ( no );
    }

    public List<Tag> getTagsByRegistertType ( final ModbusRegisterType registerType )
    {
        final List<Tag> result = new ArrayList<Tag> ( this.tagsByName.get ( registerType ).values () );
        Collections.sort ( result );
        return result;
    }

    public Set<ItemValue> updateBitFields ( final ModbusRegisterType registerType, final int startAddress, final int quantity, final BitSet values )
    {
        final BitSet data;
        if ( registerType == ModbusRegisterType.DiscreteInputs )
        {
            data = this.discreteInputs;
        }
        else if ( registerType == ModbusRegisterType.Coils )
        {
            data = this.coils;
        }
        else
        {
            throw new IllegalArgumentException ( "updateBitFields applies only for DiscreteInputs or Coils" );
        }
        final Set<ItemValue> updatedItems = new HashSet<ItemValue> ();
        for ( int offset = 0; offset < quantity; offset++ )
        {
            final int address = startAddress + offset;
            final Tag tag = tagByAdress ( registerType, address );
            final boolean value = values.get ( offset );
            data.set ( address, value );
            if ( tag != null )
            {
                updatedItems.add ( new ItemValue ( tag.getName (), Variant.valueOf ( value ) ) );
            }
        }
        return updatedItems;
    }

    public Set<ItemValue> updateRegisters ( final ModbusRegisterType registerType, final int startAddress, final int quantity, final byte[] values )
    {
        final ByteBuffer data;
        if ( registerType == ModbusRegisterType.InputRegisters )
        {
            data = this.inputRegisters;
        }
        else if ( registerType == ModbusRegisterType.HoldingRegisters )
        {
            data = this.holdingRegisters;
        }
        else
        {
            throw new IllegalArgumentException ( "updateRegisters applies only for InputRegisters or HoldingRegisters" );
        }
        // do update in two phases
        // 1.) update data in buffer
        final Set<Tag> tagsToUpdate = new HashSet<Tag> ();
        final int oldPosition = data.position ();
        // we need 2 bytes for each register, so address is to multiply by two
        data.position ( startAddress * 2 );
        for ( int offset = 0; offset < quantity * 2; offset++ )
        {
            final int address = startAddress + offset;
            final Tag tag = tagByAdress ( registerType, address );
            data.put ( values[offset] );
            if ( tag != null )
            {
                tagsToUpdate.add ( tag );
            }
        }
        // 2.) gather values
        final Set<ItemValue> updatedItems = new HashSet<ItemValue> ();
        for ( final Tag tag : tagsToUpdate )
        {
            updatedItems.add ( new ItemValue ( tag.getName (), getValue ( registerType, tag ) ) );
        }
        data.position ( oldPosition );
        return updatedItems;
    }

    public List<Block> calculateBlocks ( final int discreteInputOffset, final int coilOffset, final int inputRegisterOffset, final int holdingRegisterOffset )
    {
        final List<Block> result = new ArrayList<Block> ();
        // coils
        result.addAll ( calculateBlocksInternal ( ModbusRegisterType.DiscreteInputs, discreteInputOffset ) );
        result.addAll ( calculateBlocksInternal ( ModbusRegisterType.Coils, coilOffset ) );
        // registers
        result.addAll ( calculateBlocksInternal ( ModbusRegisterType.InputRegisters, inputRegisterOffset ) );
        result.addAll ( calculateBlocksInternal ( ModbusRegisterType.HoldingRegisters, holdingRegisterOffset ) );

        return result;
    }

    private List<Block> calculateBlocksInternal ( final ModbusRegisterType registerType, final int offset )
    {
        final List<Block> result = new ArrayList<Block> ();
        final List<Tag> tags = getTagsByRegistertType ( registerType );
        int startAddress = -1;
        int endAddress = -1;
        int priority = -1;
        for ( int index = 0; index < tags.size (); index++ )
        {
            final Tag currentTag = tags.get ( index );
            final Tag nextTag;
            if ( index + 1 < tags.size () )
            {
                nextTag = tags.get ( index + 1 );
            }
            else
            {
                nextTag = currentTag;
            }
            if ( startAddress == -1 )
            {
                startAddress = currentTag.getAddress ();
                endAddress = currentTag.getAddress () + currentTag.getType ().getSize ();
                priority = currentTag.getPriority ();
            }
            // next tag doesn't fit in current block
            if ( nextTag.getAddress () + nextTag.getType ().getSize () - startAddress > registerType.getMaxElementsToQuery () )
            {
                final Block block = new Block ( registerType, startAddress, offset, endAddress - startAddress, priority );
                result.add ( block );
                startAddress = -1;
                endAddress = -1;
                priority = -1;
            }
            else
            {
                endAddress = nextTag.getAddress () + nextTag.getType ().getSize ();
                if ( nextTag.getPriority () < priority )
                {
                    priority = nextTag.getPriority ();
                }
            }
            // reached end of list
            if ( index + 1 == tags.size () )
            {
                final Block block = new Block ( registerType, startAddress, offset, endAddress - startAddress, priority );
                result.add ( block );
            }
        }
        return Collections.unmodifiableList ( result );
    }
}
