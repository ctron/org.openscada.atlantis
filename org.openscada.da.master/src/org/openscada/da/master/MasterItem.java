package org.openscada.da.master;

import org.openscada.da.datasource.DataSource;


public interface MasterItem extends DataSource
{

    /**
     * remove sub condition
     * @param type the type of the handler that should be removed
     * @return 
     */
    public abstract void removeHandler ( final MasterItemHandler handler );

    /**
     * Add a new sub condition
     * @param handler new condition to add
     */
    public abstract void addHandler ( final MasterItemHandler handler );
}