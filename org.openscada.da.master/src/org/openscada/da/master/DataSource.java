package org.openscada.da.master;

public interface DataSource
{
    public abstract void addListener ( final DataSourceListener listener );

    public abstract void removeListener ( final DataSourceListener listener );
}
