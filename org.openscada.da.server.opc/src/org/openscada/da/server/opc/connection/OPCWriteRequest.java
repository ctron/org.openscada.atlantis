/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.opc.connection;

import org.jinterop.dcom.core.JIVariant;

public class OPCWriteRequest
{
    private final JIVariant value;

    private final String itemId;

    public OPCWriteRequest ( final String itemId, final JIVariant value )
    {
        this.value = value;
        this.itemId = itemId;
    }

    public JIVariant getValue ()
    {
        return this.value;
    }

    public String getItemId ()
    {
        return this.itemId;
    }

    @Override
    public String toString ()
    {
        return String.format ( "[OPCWriteRequest - itemId: %s, value: %s]", this.itemId, this.value );
    }
}
