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

/**
 * used for internal representation of
 * 
 * @author jrose
 */
public enum ModbusRegisterType
{
    DiscreteInputs ( ModbusConstants.MAX_COILS_PER_REQUEST ),
    Coils ( ModbusConstants.MAX_COILS_PER_REQUEST ),
    InputRegisters ( ModbusConstants.MAX_REGISTERS_PER_REQUEST ),
    HoldingRegisters ( ModbusConstants.MAX_REGISTERS_PER_REQUEST );

    private final int maxElementsToQuery;

    ModbusRegisterType ( final int maxElementsToQuery )
    {
        this.maxElementsToQuery = maxElementsToQuery;
    }

    public int getMaxElementsToQuery ()
    {
        return this.maxElementsToQuery;
    }
}
