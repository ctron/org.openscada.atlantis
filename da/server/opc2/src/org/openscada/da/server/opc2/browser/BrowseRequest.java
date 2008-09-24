package org.openscada.da.server.opc2.browser;

import java.util.Collection;

public class BrowseRequest
{
    private Collection<String> path;

    public BrowseRequest ( Collection<String> path )
    {
        this.path = path;
    }

    public Collection<String> getPath ()
    {
        return path;
    }

    public void setPath ( Collection<String> path )
    {
        this.path = path;
    }
}
