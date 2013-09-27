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

public class WriteMultipleRegistersResponse extends AddressableResponseMessage
{
    public WriteMultipleRegistersResponse ( final int startAddress )
    {
        super ( ModbusConstants.FUNCTION_CODE_WRITE_MULTIPLE_REGISTERS, startAddress );
    }
}
