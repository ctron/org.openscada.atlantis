package org.openscada.da.core.browser;

import org.openscada.da.core.InvalidSessionException;
import org.openscada.da.core.Session;

public interface HiveBrowser
{
    void subscribe ( Session session, Location location ) throws NoSuchFolderException, InvalidSessionException;
    void unsubscribe ( Session session, Location location ) throws NoSuchFolderException, InvalidSessionException;
    
    Entry[] list ( Session session, Location location ) throws InvalidSessionException, NoSuchFolderException;
}
