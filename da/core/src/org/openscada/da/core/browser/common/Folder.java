package org.openscada.da.core.browser.common;

import java.util.Stack;

import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.NoSuchFolderException;


public interface Folder
{
    Entry [] list ( Stack<String> path ) throws NoSuchFolderException;
    
    void subscribe ( Stack<String> path, FolderListener listener, Object tag ) throws NoSuchFolderException;
    void unsubscribe ( Stack<String> path, Object tag ) throws NoSuchFolderException;
}
