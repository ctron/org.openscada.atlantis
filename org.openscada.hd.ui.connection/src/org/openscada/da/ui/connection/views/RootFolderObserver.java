package org.openscada.da.ui.connection.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.openscada.core.ui.connection.data.ConnectionHolder;
import org.openscada.da.connection.provider.ConnectionService;

public class RootFolderObserver extends FolderObserver implements PropertyChangeListener
{
    final ConnectionHolder connectionHolder;

    public RootFolderObserver ( final ConnectionHolder connectionHolder )
    {
        this.connectionHolder = connectionHolder;
        connectionHolder.addPropertyChangeListener ( ConnectionHolder.PROP_CONNECTION_SERVICE, this );
    }

    @Override
    public synchronized void dispose ()
    {
        this.connectionHolder.removePropertyChangeListener ( ConnectionHolder.PROP_CONNECTION_SERVICE, this );
        setFolderManager ( null );
        super.dispose ();
    }

    public void propertyChange ( final PropertyChangeEvent evt )
    {
        final org.openscada.core.connection.provider.ConnectionService connection = this.connectionHolder.getConnectionService ();
        if ( connection == null )
        {
            setConnection ( null );
        }
        else if ( connection instanceof ConnectionService )
        {
            setConnection ( (ConnectionService)connection );
        }
    }

    private void setConnection ( final ConnectionService connectionService )
    {
        if ( connectionService != null )
        {
            setFolderManager ( new FolderEntryWrapper ( connectionService.getFolderManager () ) );
        }
        else
        {
            clear ();
        }
    }
}
