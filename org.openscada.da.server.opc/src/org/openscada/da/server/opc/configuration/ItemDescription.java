/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.opc.configuration;

public class ItemDescription
{
    private final String id;

    private final String description;

    private final String accessPath;

    public ItemDescription ( final String id, final String description, final String accessPath )
    {
        this.id = id;
        this.description = description;
        this.accessPath = accessPath;
    }

    public ItemDescription ( final String id )
    {
        this.id = id;
        this.description = null;
        this.accessPath = null;
    }

    public String getId ()
    {
        return this.id;
    }

    public String getDescription ()
    {
        return this.description;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.id == null ? 0 : this.id.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass () != obj.getClass () )
        {
            return false;
        }
        final ItemDescription other = (ItemDescription)obj;
        if ( this.id == null )
        {
            if ( other.id != null )
            {
                return false;
            }
        }
        else if ( !this.id.equals ( other.id ) )
        {
            return false;
        }
        return true;
    }

    public String getAccessPath ()
    {
        return this.accessPath;
    }
}
