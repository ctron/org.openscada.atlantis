/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
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
