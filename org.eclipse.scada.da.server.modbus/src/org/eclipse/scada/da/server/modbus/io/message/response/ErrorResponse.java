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

public class ErrorResponse extends ResponseMessage
{
    private final byte exceptionCode;

    public ErrorResponse ( final byte functionCode, final byte exceptionCode )
    {
        super ( functionCode );
        this.exceptionCode = exceptionCode;
    }

    public byte getExceptionCode ()
    {
        return this.exceptionCode;
    }

    @Override
    public String toString ()
    {
        return "ErrorResponse [functionCode=" + ( getFunctionCode () - (byte)0x80 ) + ", exceptionCode=" + this.exceptionCode + "]";
    }
}
