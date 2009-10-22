package org.openscada.ae.ui.views;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.ObservableSet;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.databinding.viewers.ObservableSetTreeContentProvider;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.openscada.ae.ui.Activator;
import org.openscada.ae.ui.data.ConnectionEntryBean;

public class ConnectionsView extends ViewPart
{

    private final class TreeStructureAdvisorExtension extends TreeStructureAdvisor
    {

    }

    private TreeViewer viewer;

    private ObservableSet connections;

    public ConnectionsView ()
    {
    }

    @Override
    public void createPartControl ( final Composite parent )
    {
        this.connections = Activator.getDefault ().getConnectionSet ();

        this.viewer = new TreeViewer ( parent );
        this.viewer.setContentProvider ( new ObservableSetTreeContentProvider ( new IObservableFactory () {

            public IObservable createObservable ( final Object target )
            {
                if ( target instanceof IObservable )
                {
                    return (IObservable)target;
                }
                else if ( target instanceof ConnectionEntryBean )
                {
                    return ( (ConnectionEntryBean)target ).getEntries ();
                }
                return null;
            }
        }, new TreeStructureAdvisorExtension () ) );
        this.viewer.setLabelProvider ( new LabelProvider ( BeansObservables.observeMap ( this.connections, ConnectionEntryBean.class, "connection" ), BeansObservables.observeMap ( this.connections, ConnectionEntryBean.class, "connectionStatus" ) ) );
        this.viewer.setInput ( this.connections );

        getSite ().setSelectionProvider ( this.viewer );

        hookContextMenu ();
    }

    @Override
    public void setFocus ()
    {
        this.viewer.getControl ().setFocus ();
    }

    private void hookContextMenu ()
    {
        final MenuManager menuMgr = new MenuManager ( "#PopupMenu" );
        menuMgr.setRemoveAllWhenShown ( true );
        menuMgr.addMenuListener ( new IMenuListener () {
            public void menuAboutToShow ( final IMenuManager manager )
            {
                fillContextMenu ( manager );
            }
        } );
        final Menu menu = menuMgr.createContextMenu ( this.viewer.getControl () );
        this.viewer.getControl ().setMenu ( menu );
        getSite ().registerContextMenu ( menuMgr, this.viewer );
    }

    private void fillContextMenu ( final IMenuManager manager )
    {
        // Other plug-ins can contribute there actions here
        manager.add ( new Separator () );
        manager.add ( new Separator ( IWorkbenchActionConstants.MB_ADDITIONS ) );
    }

}
