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

package org.openscada.da.server.modbus;

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
