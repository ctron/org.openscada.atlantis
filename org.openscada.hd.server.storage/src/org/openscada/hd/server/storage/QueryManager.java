package org.openscada.hd.server.storage;

import org.openscada.hd.server.storage.internal.QueryImpl;

public interface QueryManager
{
    public void removeQuery ( final QueryImpl query );
}
