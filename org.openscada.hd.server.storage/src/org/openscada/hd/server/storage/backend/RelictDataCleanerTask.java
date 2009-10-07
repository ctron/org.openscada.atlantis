package org.openscada.hd.server.storage.backend;

import java.util.TimerTask;

/**
 * This task is used to periodically delete old data.
 * @author Ludwig Straub
 */
public class RelictDataCleanerTask extends TimerTask
{
    /** Back end of which old data has to be deleted. */
    private final RelictDataCleaner relictCleaner;

    /**
     * Constructor.
     * @param relictCleaner back end of which old data has to be deleted
     */
    public RelictDataCleanerTask ( RelictDataCleaner relictCleaner )
    {
        this.relictCleaner = relictCleaner;
    }

    /**
     * This method performs the cleaning actions.
     */
    public void run ()
    {
        relictCleaner.deleteRelicts ();
    }
}
