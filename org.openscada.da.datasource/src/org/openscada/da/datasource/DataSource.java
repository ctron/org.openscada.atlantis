package org.openscada.da.datasource;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.utils.concurrent.NotifyFuture;

public interface DataSource
{
    public static final String DATA_SOURCE_ID = "datasource.id";

    public abstract void addListener ( final DataSourceListener listener );

    public abstract void removeListener ( final DataSourceListener listener );

    public abstract NotifyFuture<WriteResult> startWriteValue ( final WriteInformation writeInformation, final Variant value );

    public abstract NotifyFuture<WriteAttributeResults> startWriteAttributes ( final WriteInformation writeInformation, final Map<String, Variant> attributes );
}
