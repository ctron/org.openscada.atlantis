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
package org.eclipse.scada.da.server.modbus;

import java.util.ArrayList;
import java.util.List;

public class ModbusConstants
{
    public static final int MIN_SLAVE_ID = 1;

    public static final int MAX_SLAVE_ID = 247;

    public static final int MIN_ADDRESS = 0x0000;

    public static final int MAX_ADDRESS = 0xffff;

    public static final int MAX_COILS_PER_REQUEST = 2000;

    public static final int MAX_REGISTERS_PER_REQUEST = 125;

    public static final int MAX_PDU_SIZE = 253;

    // implemented
    public static final byte FUNCTION_CODE_READ_DISCRETE_INPUTS = 0x02;

    public static final byte FUNCTION_CODE_READ_COILS = 0x01;

    public static final byte FUNCTION_CODE_WRITE_SINGLE_COIL = 0x05;

    public static final byte FUNCTION_CODE_READ_INPUT_REGISTERS = 0x04;

    public static final byte FUNCTION_CODE_READ_HOLDING_REGISTERS = 0x03;

    public static final byte FUNCTION_CODE_WRITE_SINGLE_REGISTER = 0x06;

    public static final byte FUNCTION_CODE_WRITE_MULTIPLE_REGISTERS = 0x10;

    public static final byte FUNCTION_CODE_ERROR_BASE = (byte)0x80;

    public static final List<Byte> IMPLEMENTED_FUNCTIONS = new ArrayList<Byte> ();

    static
    {
        IMPLEMENTED_FUNCTIONS.add ( FUNCTION_CODE_READ_DISCRETE_INPUTS );

        IMPLEMENTED_FUNCTIONS.add ( FUNCTION_CODE_READ_COILS );
        IMPLEMENTED_FUNCTIONS.add ( FUNCTION_CODE_WRITE_SINGLE_COIL );

        IMPLEMENTED_FUNCTIONS.add ( FUNCTION_CODE_READ_INPUT_REGISTERS );

        IMPLEMENTED_FUNCTIONS.add ( FUNCTION_CODE_READ_HOLDING_REGISTERS );
        IMPLEMENTED_FUNCTIONS.add ( FUNCTION_CODE_WRITE_MULTIPLE_REGISTERS );
    }
}
