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

import org.eclipse.scada.da.server.modbus.ModbusConstants;

public class ReadDiscreteInputsRequest extends AddressableRequestMessage
{
    public ReadDiscreteInputsRequest ( final int startAddress, final int offset, final int quantity )
    {
        super ( ModbusConstants.FUNCTION_CODE_READ_DISCRETE_INPUTS, startAddress, offset, quantity );
    }
}
