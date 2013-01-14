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

package org.openscada.da.server.modbus.io.message.request;

public class AddressableRequestMessage extends RequestMessage
{
    private final int startAddress;

    private final int offset;

    private final int quantity;

    public AddressableRequestMessage ( final byte functionCode, final int startAddress, final int offset, final int quantity )
    {
        super ( functionCode );
        this.startAddress = startAddress;
        this.offset = offset;
        this.quantity = quantity;
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

    @Override
    public String toString ()
    {
        return getClass ().getSimpleName () + " [functionCode=" + getFunctionCode () + ", startAddress=" + this.startAddress + ", quantity=" + this.quantity + "]";
    }
}
