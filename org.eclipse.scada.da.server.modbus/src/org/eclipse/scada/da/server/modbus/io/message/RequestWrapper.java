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

public class RequestWrapper
{
    private final byte unitIdentifier;

    private final RequestMessage message;

    public RequestWrapper ( final byte unitIdentifier, final RequestMessage message )
    {
        this.unitIdentifier = unitIdentifier;
        this.message = message;
    }

    public byte getUnitIdentifier ()
    {
        return this.unitIdentifier;
    }

    public RequestMessage getMessage ()
    {
        return this.message;
    }

    @Override
    public String toString ()
    {
        return "RequestWrapper [message=" + this.message + ", unitIdentifier=" + this.unitIdentifier + "]";
    }
}
