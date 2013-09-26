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
