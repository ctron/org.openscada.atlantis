package org.openscada.hd.server.storage.relict;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This task is used to periodically delete old data.
 * @author Ludwig Straub
 */
public class RelictCleanerCallerTask extends TimerTask
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( RelictCleanerCallerTask.class );

    /** Object of which old data has to be deleted. */
    private final RelictCleaner relictCleaner;

    /**
     * Constructor.
     * @param backEnd Object of which old data has to be deleted
     */
    public RelictCleanerCallerTask ( RelictCleaner relictCleaner )
    {
        this.relictCleaner = relictCleaner;
    }

    /**
     * This method performs the cleaning actions.
     */
    public void run ()
    {
        try
        {
            relictCleaner.cleanupRelicts ();
        }
        catch ( Exception e )
        {
            logger.warn ( "error while cleaning relicts", e );
        }
    }
}
