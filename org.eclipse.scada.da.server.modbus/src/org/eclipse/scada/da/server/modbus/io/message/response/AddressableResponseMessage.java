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
package org.eclipse.scada.da.server.modbus.io.message.response;

public class AddressableResponseMessage extends ResponseMessage
{
    private final int startAddress;

    public AddressableResponseMessage ( final byte functionCode, final int startAddress )
    {
        super ( functionCode );
        this.startAddress = startAddress;
    }

    public int getStartAddress ()
    {
        return this.startAddress;
    }

    @Override
    public String toString ()
    {
        return getClass ().getSimpleName () + " [functionCode=" + getFunctionCode () + ", startAddress=" + this.startAddress + "]";
    }
}
