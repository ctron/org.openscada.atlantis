package org.openscada.ae.storage.common;

import org.apache.log4j.Logger;
import org.openscada.ae.storage.Session;

public class SessionCommon implements Session
{
    private static Logger logger = Logger.getLogger ( SessionCommon.class );

    private final StorageCommon storage;

    public SessionCommon ( final StorageCommon storage )
    {
        this.storage = storage;
    }

    public void dispose ()
    {
        logger.info ( "Disposing session..." );
    }

}
