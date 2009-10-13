package org.openscada.hd.ui.connection.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.observable.set.WritableSet;
import org.openscada.core.ui.connection.data.ConnectionHolder;
import org.openscada.hd.connection.provider.ConnectionService;

public class ConnectionWrapper extends WritableSet implements PropertyChangeListener
{

    private final ConnectionHolder holder;

    private ConnectionService service;

    public ConnectionWrapper ( final ConnectionHolder target )
    {
        this.holder = target;
        this.holder.addPropertyChangeListener ( ConnectionHolder.PROP_CONNECTION_SERVICE, this );
    }

    @Override
    public synchronized void dispose ()
    {
        this.holder.removePropertyChangeListener ( ConnectionHolder.PROP_CONNECTION_SERVICE, this );
        super.dispose ();
    }

    public void propertyChange ( final PropertyChangeEvent evt )
    {
        getRealm ().asyncExec ( new Runnable () {

            public void run ()
            {
                update ();
            }
        } );
        update ();
    }

    private void update ()
    {
        setStale ( true );

        try
        {
            clear ();
            this.service = null;

            final ConnectionService service = (ConnectionService)this.holder.getConnectionService ();
            this.service = service;
            add ( "Items" );
            add ( "Queries" );
        }
        finally
        {
            setStale ( false );
        }
    }

}
