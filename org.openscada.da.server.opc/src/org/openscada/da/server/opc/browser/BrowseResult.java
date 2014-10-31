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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class BrowseResult
{
    private Collection<String> branches = new LinkedList<String> ();

    private Collection<BrowseResultEntry> leaves = new ArrayList<BrowseResultEntry> ();

    public Collection<String> getBranches ()
    {
        return this.branches;
    }

    public void setBranches ( final Collection<String> branches )
    {
        this.branches = branches;
    }

    public Collection<BrowseResultEntry> getLeaves ()
    {
        return this.leaves;
    }

    public void setLeaves ( final Collection<BrowseResultEntry> leaves )
    {
        this.leaves = leaves;
    }
}
