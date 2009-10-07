package org.openscada.hd.server.storage.backend.filefilter;

import java.io.File;
import java.io.FileFilter;

/**
 * This file filter searches for files matching files with the specified name.
 * @author Ludwig Straub
 */
public class FileFileFilter implements FileFilter
{
    /** Case insensitive name of the files that have to be searched. If null is set then all files are accepted. */
    private final String fileNamePattern;

    /**
     * Constructor
     * @param fileNamePattern case pattern of file names that have to be searched. If null is passed then all files are accepted
     */
    public FileFileFilter ( final String fileNamePattern )
    {
        this.fileNamePattern = fileNamePattern;
    }

    /**
     * @see java.io.FileFilter#accept
     */
    public boolean accept ( final File file )
    {
        return file.isFile () && ( ( fileNamePattern == null ) || file.getName ().matches ( fileNamePattern ) );
    }
}
