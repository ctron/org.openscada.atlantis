package org.openscada.hd.server.storage.backend.filefilter;

import java.io.File;
import java.io.FileFilter;

/**
 * This file filter searches for sub directories with the specified name.
 * @author Ludwig Straub
 */
public class DirectoryFileFilter implements FileFilter
{
    /** Case insensitive name of the sub directories that have to be searched. If null is set then all directories are accepted. */
    private final String name;

    /**
     * Constructor
     * @param name case insensitive name of the sub directories that have to be searched. If null is passed then all directories are accepted
     */
    public DirectoryFileFilter ( final String name )
    {
        this.name = name;
    }

    /**
     * @see java.io.FileFilter#accept
     */
    public boolean accept ( final File file )
    {
        return file.isDirectory () && ( ( name == null ) || file.getName ().equalsIgnoreCase ( name ) );
    }
}
