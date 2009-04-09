package org.openscada.da.server.exec2.command;

import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.exec2.Hive;

public interface ContinuousCommand
{
    public void start ( Hive hive, FolderCommon parentFolder );

    public void stop ();
}
