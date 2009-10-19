package org.openscada.hd.ui.connection.views;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.observable.set.WritableSet;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.ItemListListener;
import org.openscada.hd.connection.provider.ConnectionService;
import org.openscada.hd.ui.connection.internal.ConnectionWrapper;
import org.openscada.hd.ui.connection.internal.ItemWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemListObserver extends WritableSet implements ItemListListener
{

    private final static Logger logger = LoggerFactory.getLogger ( ItemListObserver.class );

    private final ConnectionService service;

    private final ConnectionWrapper connection;

    private final Map<String, ItemWrapper> items = new HashMap<String, ItemWrapper> ();

    public ItemListObserver ( final ConnectionWrapper connection )
    {
        this.connection = connection;
        this.service = connection.getService ();
        synchronized ( this )
        {
            this.service.getConnection ().addListListener ( this );
        }
    }

    @Override
    public synchronized void dispose ()
    {
        this.service.getConnection ().removeListListener ( this );
        super.dispose ();
    }

    public void listChanged ( final Set<HistoricalItemInformation> addedOrModified, final Set<String> removed, final boolean full )
    {
        logger.debug ( "List changed: {} / {}", new Object[] { addedOrModified, removed } );

        if ( !isDisposed () )
        {
            getRealm ().asyncExec ( new Runnable () {
                public void run ()
                {
                    handleUpdate ( addedOrModified, removed, full );
                }
            } );
        }
    }

    protected void handleUpdate ( final Set<HistoricalItemInformation> addedOrModified, final Set<String> removed, final boolean full )
    {
        if ( isDisposed () )
        {
            return;
        }

        setStale ( true );
        try
        {
            if ( full )
            {
                // full transmission ... clear first
                clear ();
            }

            if ( removed != null )
            {
                for ( final String itemId : removed )
                {
                    final ItemWrapper info = this.items.remove ( itemId );
                    if ( info != null )
                    {
                        remove ( info );
                    }
                }
            }
            if ( addedOrModified != null )
            {
                for ( final HistoricalItemInformation item : addedOrModified )
                {
                    final ItemWrapper wrapper = new ItemWrapper ( this.connection, item );
                    this.items.put ( item.getId (), wrapper );
                    add ( wrapper );
                }
            }
        }
        finally
        {
            setStale ( false );
        }
    }
}
