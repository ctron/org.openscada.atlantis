package org.openscada.hd.ui.views;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.UnionSet;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.databinding.viewers.ObservableSetTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.openscada.hd.ui.Activator;
import org.openscada.hd.ui.data.ConnectionEntryBean;

public class ConnectionView extends ViewPart
{

    private TreeViewer viewer;

    public ConnectionView ()
    {
    }

    @Override
    public void createPartControl ( final Composite parent )
    {

        this.viewer = new TreeViewer ( parent );

        final ObservableSetTreeContentProvider contentProvider;

        contentProvider = new ObservableSetTreeContentProvider ( new IObservableFactory () {

            public IObservable createObservable ( final Object target )
            {
                if ( target instanceof IObservable )
                {
                    return (IObservable)target;
                }
                else if ( target instanceof ConnectionEntryBean )
                {
                    return new UnionSet ( new IObservableSet[] { ( (ConnectionEntryBean)target ).getEntries (), ( (ConnectionEntryBean)target ).getQueries () } );
                }
                return null;
            }
        }, null );

        this.viewer.setContentProvider ( contentProvider );

        this.viewer.setLabelProvider ( new LabelProvider ( //
        BeansObservables.observeMap ( contentProvider.getKnownElements (), "connection" ), //
        BeansObservables.observeMap ( contentProvider.getKnownElements (), "connectionStatus" ), //
        BeansObservables.observeMap ( contentProvider.getKnownElements (), "state" ) // 
        ) );
        this.viewer.setInput ( Activator.getDefault ().getConnectionSet () );

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
