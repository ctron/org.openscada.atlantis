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
package org.eclipse.scada.da.server.modbus.io.message;

import org.eclipse.scada.da.server.modbus.io.message.request.RequestMessage;
import org.eclipse.scada.da.server.modbus.io.message.response.ResponseMessage;

public class ResponseWrapper
{
    private final byte unitIdentifier;

    private final ResponseMessage message;

    private final RequestMessage originalRequest;

    public ResponseWrapper ( final byte unitIdentifier, final ResponseMessage message, final RequestMessage request )
    {
        this.unitIdentifier = unitIdentifier;
        this.message = message;
        this.originalRequest = request;
    }

    public byte getUnitIdentifier ()
    {
        return this.unitIdentifier;
    }

    public ResponseMessage getMessage ()
    {
        return this.message;
    }

    public RequestMessage getOriginalRequest ()
    {
        return this.originalRequest;
    }

    @Override
    public String toString ()
    {
        return "ResponseWrapper [message=" + this.message + ", originalRequest=" + this.originalRequest + ", unitIdentifier=" + this.unitIdentifier + "]";
    }
}
