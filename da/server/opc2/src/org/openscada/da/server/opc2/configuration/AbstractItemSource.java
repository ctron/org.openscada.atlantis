package org.openscada.da.server.opc2.configuration;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;

public abstract class AbstractItemSource implements ItemSource
{
    private static Logger logger = Logger.getLogger ( AbstractItemSource.class );

    private Set<ItemSourceListener> listeners = new CopyOnWriteArraySet<ItemSourceListener> ();

    public void addListener ( ItemSourceListener listener )
    {
        listeners.add ( listener );
    }

    public void removeListener ( ItemSourceListener listener )
    {
        listeners.remove ( listener );
    }

    protected void fireAvailableItemsChanged ( Set<ItemDescription> items )
    {
        for ( ItemSourceListener listener : this.listeners )
        {
            try
            {
                listener.availableItemsChanged ( items );
            }
            catch ( Throwable e )
            {
                logger.info ( "Failed to handle availableItemsChanged", e );
            }
        }
    }

    public abstract void activate ();
    
    public void deactivate ()
    {
        this.listeners.clear ();
    }

}
