package org.openscada.hd.server.storage.relict;

/**
 * This interface provides a method for triggering of cleanup operations.
 * @author Ludwig Sickinger
 */
public interface RelictCleaner
{
    /**
     * This method deletes old data.
     * This method can only be called after the initialize method.
     * @throws Exception in case of any problem
     */
    public abstract void cleanupRelicts () throws Exception;
}
