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

import java.util.BitSet;

import org.eclipse.scada.da.server.modbus.ModbusConstants;

public class ReadCoilsResponse extends AddressableResponseMessage
{
    private final BitSet bitset;

    public ReadCoilsResponse ( final int startAddress, final BitSet bits )
    {
        super ( ModbusConstants.FUNCTION_CODE_READ_COILS, startAddress );
        this.bitset = bits;
    }

    public BitSet getBitset ()
    {
        final BitSet result = new BitSet ( this.bitset.length () );
        result.or ( this.bitset );
        return result;
    }
}
