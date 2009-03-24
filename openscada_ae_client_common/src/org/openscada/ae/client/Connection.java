package org.openscada.ae.client;

import java.util.Collection;

import org.openscada.ae.core.QueryDescriptor;

public interface Connection extends org.openscada.core.client.Connection
{
    public abstract Collection<QueryDescriptor> listQueries ();

    public abstract void listQueries ( final ListQueryListener listener );

    public abstract void setQueryListener ( String queryId, final QueryListener listener );
}
