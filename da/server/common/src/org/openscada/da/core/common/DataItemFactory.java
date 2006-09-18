package org.openscada.da.core.common;

public interface DataItemFactory
{
    public boolean canCreate ( String id );
    public DataItem create ( String id );
}
