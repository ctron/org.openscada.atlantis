package org.openscada.hd.server.storage.backend;

/**
 * This interface defines a method for cleaning old data.
 * @author Ludwig Straub
 */
public interface RelictDataCleaner
{
    /**
     * This method deletes old data.
     */
    public abstract void deleteRelicts ();
}
