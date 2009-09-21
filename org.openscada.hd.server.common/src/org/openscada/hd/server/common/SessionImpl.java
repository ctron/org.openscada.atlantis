package org.openscada.hd.server.common;

import org.openscada.hd.ItemListListener;
import org.openscada.hd.server.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionImpl implements Session
{

    private final static Logger logger = LoggerFactory.getLogger ( SessionImpl.class );

    private final String user;

    public SessionImpl ( final String user )
    {
        logger.info ( "Created new session" );

        this.user = user;

    }

    public void dispose ()
    {
        logger.info ( "Disposing session" );
    }

    public String getCurrentUser ()
    {
        return this.user;
    }

    public void setItemListListener ( final ItemListListener itemListListener )
    {

    }
}
