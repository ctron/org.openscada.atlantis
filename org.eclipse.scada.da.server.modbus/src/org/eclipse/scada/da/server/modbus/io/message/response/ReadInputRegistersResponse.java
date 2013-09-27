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

import org.eclipse.scada.da.server.modbus.ModbusConstants;

public class ReadInputRegistersResponse extends AddressableResponseMessage
{
    private final byte[] registers;

    public ReadInputRegistersResponse ( final int startAddress, final byte[] registers )
    {
        super ( ModbusConstants.FUNCTION_CODE_READ_INPUT_REGISTERS, startAddress );
        this.registers = registers.clone ();
    }

    public byte[] getRegisters ()
    {
        return this.registers;
    }
}