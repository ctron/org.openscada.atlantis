package org.openscada.da.datasource.testing;

import java.util.Map;

import org.openscada.da.datasource.DataSource;

public interface DefaultDataSource extends DataSource
{
    public abstract void dispose ();

    public abstract void update ( Map<String, String> properties );
}
