/*******************************************************************************
 * Copyright (c) 2013 TH4 SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     TH4 SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.scada.da.server.modbus.io.message.request;

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
