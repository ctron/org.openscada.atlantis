/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.opc.connection;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscada.opc.dcom.common.Result;
import org.openscada.opc.dcom.da.WriteRequest;
import org.openscada.utils.concurrent.FutureTask;

public class OPCIoContext
{
    private Map<String, Boolean> activations;

    private Collection<ItemRegistrationRequest> registrations;

    private Collection<FutureTask<Result<WriteRequest>>> writeRequests;

    private Set<String> unregistrations;

    private Set<String> readItems;

    public Map<String, Boolean> getActivations ()
    {
        return this.activations;
    }

    public void setActivations ( final Map<String, Boolean> activations )
    {
        this.activations = activations;
    }

    public Collection<ItemRegistrationRequest> getRegistrations ()
    {
        return this.registrations;
    }

    public void setRegistrations ( final Collection<ItemRegistrationRequest> registrations )
    {
        this.registrations = registrations;
    }

    public Collection<FutureTask<Result<WriteRequest>>> getWriteRequests ()
    {
        return this.writeRequests;
    }

    public void setWriteRequests ( final List<FutureTask<Result<WriteRequest>>> writeRequests )
    {
        this.writeRequests = writeRequests;
    }

    public Set<String> getReadItems ()
    {
        return this.readItems;
    }

    public void setReadItems ( final Set<String> readItems )
    {
        this.readItems = readItems;
    }

    public Set<String> getUnregistrations ()
    {
        return this.unregistrations;
    }

    public void setUnregistrations ( final Set<String> unregistrations )
    {
        this.unregistrations = unregistrations;
    }

}
