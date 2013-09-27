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

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.server.modbus.ModbusConstants;

public class WriteSingleCoilRequest extends AddressableRequestMessage
{
    final private Variant value;

    public WriteSingleCoilRequest ( final int startAddress, final int offset, final Variant value )
    {
        super ( ModbusConstants.FUNCTION_CODE_WRITE_SINGLE_COIL, startAddress, offset, 1 );
        this.value = value;
    }

    public Variant getValue ()
    {
        return this.value;
    }
}
