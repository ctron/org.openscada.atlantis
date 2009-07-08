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
