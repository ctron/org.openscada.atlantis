package org.openscada.da.datasource;

import org.openscada.core.VariantType;
import org.openscada.da.client.DataItemValue;

public interface DataSourceHandler
{

    public abstract void dispose ();

    public abstract DataItemValue getValue ();

    public abstract VariantType getType ();

}