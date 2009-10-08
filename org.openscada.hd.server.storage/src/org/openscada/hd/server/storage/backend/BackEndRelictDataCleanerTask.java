package org.openscada.hd.server.storage.backend;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This task is used to periodically delete old data.
 * @author Ludwig Straub
 */
public class BackEndRelictDataCleanerTask extends TimerTask
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( BackEndRelictDataCleanerTask.class );

    /** Back end of which old data has to be deleted. */
    private final BackEnd backEnd;

    /**
     * Constructor.
     * @param backEnd back end of which old data has to be deleted
     */
    public BackEndRelictDataCleanerTask ( BackEnd backEnd )
    {
        this.backEnd = backEnd;
    }

    /**
     * This method performs the cleaning actions.
     */
    public void run ()
    {
        try
        {
            backEnd.cleanupRelicts ();
        }
        catch ( Exception e )
        {
            logger.warn ( "error while cleaning relicts", e );
        }
    }
}
