/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.opc.browser;

import java.util.EnumSet;

import org.eclipse.scada.da.data.IODirection;

public class BrowseResultEntry
{
    private String entryName;

    private String itemId;

    private EnumSet<IODirection> ioDirections;

    public String getEntryName ()
    {
        return this.entryName;
    }

    public void setEntryName ( final String entryName )
    {
        this.entryName = entryName;
    }

    public String getItemId ()
    {
        return this.itemId;
    }

    public void setItemId ( final String itemId )
    {
        this.itemId = itemId;
    }

    public EnumSet<IODirection> getIoDirections ()
    {
        return this.ioDirections;
    }

    public void setIoDirections ( final EnumSet<IODirection> ioDirections )
    {
        this.ioDirections = ioDirections;
    }
}
