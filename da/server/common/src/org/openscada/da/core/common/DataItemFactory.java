package org.openscada.da.core.common;

public interface DataItemFactory
{
    boolean canCreate ( String id );
    DataItem create ( String id );
}
