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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.scada.utils.concurrent.FutureTask;
import org.openscada.opc.dcom.common.Result;
import org.openscada.opc.dcom.da.WriteRequest;

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
