package org.eclipse.scada.da.datasource;

import org.eclipse.scada.core.VariantType;
import org.eclipse.scada.da.client.DataItemValue;

public interface DataSourceHandler
{

    public abstract void dispose ();

    public abstract DataItemValue getValue ();

    public abstract VariantType getType ();

}