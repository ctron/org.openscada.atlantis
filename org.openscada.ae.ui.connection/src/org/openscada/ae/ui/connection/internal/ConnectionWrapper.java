package org.openscada.ae.ui.connection.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.observable.set.WritableSet;
import org.openscada.ae.BrowserEntry;
import org.openscada.ae.BrowserListener;
import org.openscada.ae.connection.provider.ConnectionService;
import org.openscada.core.ui.connection.data.ConnectionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionWrapper extends WritableSet implements PropertyChangeListener, BrowserListener
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionWrapper.class );

    private final ConnectionHolder holder;

    private ConnectionService service;

    private final Map<String, BrowserEntry> entries = new HashMap<String, BrowserEntry> ();

    public ConnectionWrapper ( final ConnectionHolder target )
    {
        this.holder = target;

        synchronized ( this )
        {
            this.holder.addPropertyChangeListener ( ConnectionHolder.PROP_CONNECTION_SERVICE, this );
            triggerUpdate ();
        }
    }

    @Override
    public synchronized void dispose ()
    {
        this.holder.removePropertyChangeListener ( ConnectionHolder.PROP_CONNECTION_SERVICE, this );
        super.dispose ();
    }

    public synchronized void propertyChange ( final PropertyChangeEvent evt )
    {
        triggerUpdate ();
    }

    private void triggerUpdate ()
    {
        getRealm ().asyncExec ( new Runnable () {

            public void run ()
            {
                update ();
            }
        } );
    }

    private void update ()
    {
        setStale ( true );

        try
        {
            clearConnection ();

            final ConnectionService service = (ConnectionService)this.holder.getConnectionService ();
            this.service = service;
            if ( this.service != null )
            {
                setupConnection ();
            }
        }
        finally
        {
            setStale ( false );
        }
    }

    private void setupConnection ()
    {
        this.service.getConnection ().addBrowserListener ( this );
    }

    private void clearConnection ()
    {
        clear ();
        this.service = null;
    }

    public ConnectionService getService ()
    {
        return this.service;
    }

    public void dataChanged ( final BrowserEntry[] addedOrUpdated, final String[] removed )
    {
        getRealm ().asyncExec ( new Runnable () {

            public void run ()
            {
                handleDataChanged ( addedOrUpdated, removed );

            }
        } );
    }

    protected void handleDataChanged ( final BrowserEntry[] addedOrUpdated, final String[] removed )
    {
        if ( isDisposed () )
        {
            return;
        }

        setStale ( true );
        try
        {
            if ( removed != null )
            {
                for ( final String item : removed )
                {
                    final BrowserEntry entry = this.entries.remove ( item );
                    if ( entry != null )
                    {
                        logger.debug ( "Removing: {}", entry );
                        remove ( entry );
                    }
                }
            }
            if ( addedOrUpdated != null )
            {
                for ( final BrowserEntry entry : addedOrUpdated )
                {
                    final BrowserEntry oldEntry = this.entries.put ( entry.getId (), entry );
                    if ( oldEntry != null )
                    {
                        logger.debug ( "Removing old: {}", entry.getId () );
                        remove ( oldEntry );
                    }
                    logger.debug ( "Adding: {}", entry.getId () );
                    add ( entry );
                }
            }
        }
        finally
        {
            setStale ( false );
        }
    }

}
