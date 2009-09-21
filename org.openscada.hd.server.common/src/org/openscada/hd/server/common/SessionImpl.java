package org.openscada.hd.server.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.ItemListListener;
import org.openscada.hd.server.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionImpl implements Session, ItemListListener
{

    private final static Logger logger = LoggerFactory.getLogger ( SessionImpl.class );

    private final String user;

    private final HashMap<String, HistoricalItemInformation> itemCache = new HashMap<String, HistoricalItemInformation> ();

    private ItemListListener itemListListener;

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
        synchronized ( this )
        {
            this.itemListListener = itemListListener;
            if ( itemListListener != null )
            {
                fireListChanged ( new HashSet<HistoricalItemInformation> ( this.itemCache.values () ), null, true );
            }
        }
    }

    public void listChanged ( final Set<HistoricalItemInformation> addedOrModified, final Set<String> removed, final boolean full )
    {
        synchronized ( this )
        {
            if ( full )
            {
                this.itemCache.clear ();
            }
            if ( removed != null && !full )
            {
                for ( final String item : removed )
                {
                    this.itemCache.remove ( item );
                }
            }
            if ( addedOrModified != null )
            {
                for ( final HistoricalItemInformation item : addedOrModified )
                {
                    this.itemCache.put ( item.getId (), item );
                }
            }
            fireListChanged ( addedOrModified, removed, full );
        }
    }

    protected void fireListChanged ( final Set<HistoricalItemInformation> addedOrModified, final Set<String> removed, final boolean full )
    {
        synchronized ( this )
        {
            if ( this.itemListListener != null )
            {
                this.itemListListener.listChanged ( addedOrModified, removed, full );
            }
        }
    }
}
