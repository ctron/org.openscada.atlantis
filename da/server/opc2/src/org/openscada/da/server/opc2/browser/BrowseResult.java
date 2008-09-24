package org.openscada.da.server.opc2.browser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class BrowseResult
{
    private Collection<String> branches = new LinkedList<String> ();

    private Collection<BrowseResultEntry> leaves = new ArrayList<BrowseResultEntry> ();

    public Collection<String> getBranches ()
    {
        return branches;
    }

    public void setBranches ( Collection<String> branches )
    {
        this.branches = branches;
    }

    public Collection<BrowseResultEntry> getLeaves ()
    {
        return leaves;
    }

    public void setLeaves ( Collection<BrowseResultEntry> leaves )
    {
        this.leaves = leaves;
    }
}
