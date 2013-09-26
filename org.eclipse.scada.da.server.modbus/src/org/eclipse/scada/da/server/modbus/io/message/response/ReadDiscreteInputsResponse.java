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

package org.eclipse.scada.da.server.modbus.io.message.response;

import java.util.BitSet;

import org.eclipse.scada.da.server.modbus.ModbusConstants;

public class ReadDiscreteInputsResponse extends AddressableResponseMessage
{
    private final BitSet bitset;

    public ReadDiscreteInputsResponse ( final int startAddress, final BitSet bits )
    {
        super ( ModbusConstants.FUNCTION_CODE_READ_DISCRETE_INPUTS, startAddress );
        this.bitset = new BitSet ( bits.length () );
        this.bitset.or ( bits );
    }

    public BitSet getBitset ()
    {
        final BitSet result = new BitSet ( this.bitset.length () );
        result.or ( this.bitset );
        return result;
    }
}
