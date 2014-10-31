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

import java.util.Collection;

public class BrowseRequest
{
    private Collection<String> path;

    public BrowseRequest ( final Collection<String> path )
    {
        this.path = path;
    }

    public Collection<String> getPath ()
    {
        return this.path;
    }

    public void setPath ( final Collection<String> path )
    {
        this.path = path;
    }
}
