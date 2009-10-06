package org.openscada.da.master;

public interface DataItemSource
{
    public abstract void addListener ( final MasterItemListener listener );

    public abstract void removeListener ( final MasterItemListener listener );
}
