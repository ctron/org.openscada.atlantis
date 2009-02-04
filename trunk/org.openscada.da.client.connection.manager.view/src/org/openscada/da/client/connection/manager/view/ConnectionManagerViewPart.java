package org.openscada.da.client.connection.manager.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.openscada.da.base.connection.ConnectionManager;
import org.openscada.da.base.connection.ConnectionManagerEntry;
import org.openscada.da.base.connection.ConnectionManagerListener;
import org.openscada.da.client.Connection;

public class ConnectionManagerViewPart extends ViewPart implements ConnectionManagerListener
{
    private TableViewer viewer;

    private final HashMap<Connection, ConnectionEntry> connectionMap = new HashMap<Connection, ConnectionEntry> ();

    private final List<ConnectionEntry> sourceEntries = new LinkedList<ConnectionEntry> ();

    private final WritableList entries = new WritableList ( this.sourceEntries, ConnectionEntry.class );

    private ObservableListContentProvider contentProvider;

    public ConnectionManagerViewPart ()
    {
        ConnectionManager.getDefault ().addConnectionManagerListener ( this );
    }

    @Override
    public void dispose ()
    {
        for ( final ConnectionEntry entry : this.sourceEntries )
        {
            entry.dispose ();
        }
        this.entries.clear ();
        this.connectionMap.clear ();
        super.dispose ();
    }

    @Override
    public void createPartControl ( final Composite parent )
    {
        parent.setLayout ( new FillLayout () );
        this.viewer = new TableViewer ( parent );

        final TableLayout layout = new TableLayout ();
        this.viewer.getTable ().setLayout ( layout );

        final TableViewerColumn col1 = new TableViewerColumn ( this.viewer, SWT.NONE );
        col1.getColumn ().setText ( Messages.getString("ConnectionManagerViewPart.viewer.column.connection.text") ); //$NON-NLS-1$
        layout.addColumnData ( new ColumnWeightData ( 50 ) );

        final TableViewerColumn col2 = new TableViewerColumn ( this.viewer, SWT.NONE );
        col2.getColumn ().setText ( Messages.getString("ConnectionManagerViewPart.viewer.column.state.text") ); //$NON-NLS-1$
        layout.addColumnData ( new ColumnWeightData ( 25 ) );

        this.viewer.getTable ().setHeaderVisible ( true );

        this.viewer.setContentProvider ( this.contentProvider = new ObservableListContentProvider () );

        // And a standard label provider that maps columns
        final IObservableMap[] attributeMaps = BeansObservables.observeMaps ( this.contentProvider.getKnownElements (), ConnectionEntry.class, new String[] { "connectionInformation", "connectionState" } ); //$NON-NLS-1$ //$NON-NLS-2$
        this.viewer.setLabelProvider ( new ObservableMapLabelProvider ( attributeMaps ) );

        this.viewer.setInput ( this.entries );

        connectionsAdded ( ConnectionManager.getDefault ().getConnections () );

        hookContextMenu ();
    }

    private void hookContextMenu ()
    {
        final MenuManager menuMgr = new MenuManager ( "#PopupMenu" ); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown ( true );
        menuMgr.addMenuListener ( new IMenuListener () {
            public void menuAboutToShow ( final IMenuManager manager )
            {
                ConnectionManagerViewPart.this.fillContextMenu ( manager );
            }
        } );
        final Menu menu = menuMgr.createContextMenu ( this.viewer.getControl () );
        this.viewer.getControl ().setMenu ( menu );
        getSite ().registerContextMenu ( menuMgr, this.viewer );
    }

    private void fillContextMenu ( final IMenuManager manager )
    {
        manager.add ( new Separator ( IWorkbenchActionConstants.MB_ADDITIONS ) );
    }

    @Override
    public void setFocus ()
    {
        this.viewer.getControl ().setFocus ();
    }

    public synchronized void connectionsAdded ( final Collection<ConnectionManagerEntry> connections )
    {
        this.entries.getRealm ().exec ( new Runnable () {

            public void run ()
            {
                for ( final ConnectionManagerEntry entry : connections )
                {
                    final ConnectionEntry newEntry = new ConnectionEntry ( entry.getConnection () );
                    ConnectionManagerViewPart.this.entries.add ( newEntry );
                    ConnectionManagerViewPart.this.connectionMap.put ( entry.getConnection (), newEntry );
                }
            }
        } );

    }

    public synchronized void connectionsRemoved ( final Collection<ConnectionManagerEntry> connections )
    {
        this.entries.getRealm ().exec ( new Runnable () {

            public void run ()
            {
                for ( final ConnectionManagerEntry entry : connections )
                {
                    final ConnectionEntry newEntry = ConnectionManagerViewPart.this.connectionMap.get ( entry.getConnection () );
                    if ( newEntry != null )
                    {
                        ConnectionManagerViewPart.this.entries.remove ( newEntry );
                        newEntry.dispose ();
                    }
                }
            }
        } );
    }

}
