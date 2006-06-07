package org.openscada.da.core.browser;

import java.util.Stack;


public interface Folder
{
    Entry [] list ( Stack<String> path ) throws NoSuchFolderException;
}
