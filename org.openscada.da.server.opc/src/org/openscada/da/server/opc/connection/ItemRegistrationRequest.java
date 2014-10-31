/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.opc.connection;

import java.util.Map;

import org.eclipse.scada.core.Variant;
import org.openscada.opc.dcom.da.OPCITEMDEF;

/**
 * Request for an item based on the {@link OPCITEMDEF} structure
 * @author jens
 *
 */
public class ItemRegistrationRequest
{
    private OPCITEMDEF itemDefinition;

    private Map<String, Variant> attributes;

    public OPCITEMDEF getItemDefinition ()
    {
        return this.itemDefinition;
    }

    public void setItemDefinition ( final OPCITEMDEF itemDefinition )
    {
        this.itemDefinition = itemDefinition;
    }

    public Map<String, Variant> getAttributes ()
    {
        return this.attributes;
    }

    public void setAttributes ( final Map<String, Variant> attributes )
    {
        this.attributes = attributes;
    }
}
