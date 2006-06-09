package org.openscada.da.server.snmp;

import java.util.Stack;

import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.NoSuchFolderException;
import org.openscada.da.core.browser.common.Folder;
import org.openscada.da.core.browser.common.FolderListener;

public class SNMPFolder implements Folder
{
    private SNMPNode _node = null;
    
    public SNMPFolder ( SNMPNode node )
    {
        _node = node;
    }
    
    public Entry[] list ( Stack<String> path ) throws NoSuchFolderException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void subscribe ( Stack<String> path, FolderListener listener,
            Object tag ) throws NoSuchFolderException
    {
        // TODO Auto-generated method stub

    }

    public void unsubscribe ( Stack<String> path, Object tag )
            throws NoSuchFolderException
    {
        // TODO Auto-generated method stub

    }

}
