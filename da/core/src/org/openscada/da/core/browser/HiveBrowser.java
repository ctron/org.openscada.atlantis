package org.openscada.da.core.browser;

import org.openscada.da.core.InvalidItemException;
import org.openscada.da.core.InvalidSessionException;
import org.openscada.da.core.Session;

public interface HiveBrowser
{
    void subscribe ( Session session, String[] path ) throws InvalidSessionException, InvalidItemException, NoSuchFolderException;
    void unsubscribe ( Session session, String [] path ) throws InvalidSessionException, InvalidItemException;
    
    Entry[] list ( Session session, String [] path ) throws InvalidSessionException, InvalidItemException, NoSuchFolderException;
}
