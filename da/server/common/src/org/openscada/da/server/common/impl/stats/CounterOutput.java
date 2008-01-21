package org.openscada.da.server.common.impl.stats;

import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.impl.HiveCommon;

public interface CounterOutput
{
    public abstract void setTickValue ( double average, long total );

    public abstract void register ( HiveCommon hive, FolderCommon folder, String description );

    public abstract void unregister ( HiveCommon hive, FolderCommon folder );
}
