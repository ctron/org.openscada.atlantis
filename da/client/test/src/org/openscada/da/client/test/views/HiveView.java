package org.openscada.da.client.test.views;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.openscada.da.client.test.ISharedImages;
import org.openscada.da.client.test.Openscada_da_client_testPlugin;
import org.openscada.da.client.test.actions.ConnectHiveAction;
import org.openscada.da.client.test.impl.BrowserEntry;
import org.openscada.da.client.test.impl.FolderEntry;
import org.openscada.da.client.test.impl.HiveConnection;
import org.openscada.da.client.test.impl.HiveItem;
import org.openscada.da.client.test.impl.HiveRepository;
import org.openscada.da.core.IODirection;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class HiveView extends ViewPart implements Observer
{
    private static Logger _log = Logger.getLogger ( HiveView.class );
    
    private TreeViewer _viewer;
    private DrillDownAdapter drillDownAdapter;
    
    private IViewActionDelegate connectAction;
    
    private HiveRepository _repository;
    
    class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider
    {
       
        private Viewer _viewer = null;
        private HiveRepository _repository = null;
        
        public ViewContentProvider ()
        {
        }
        
        public void inputChanged ( Viewer v, Object oldInput, Object newInput )
        {
            clearInput ();
            
            _viewer = v;
            if ( newInput instanceof HiveRepository )
            {
                _repository = (HiveRepository)newInput;
            }
        }
        
        public void clearInput ()
        {
            if ( _repository != null )
            {
                _repository = null;
            }
        }
        
        public void dispose()
        {
            clearInput ();
        }
        
        public Object[] getElements ( Object parent )
        {
            if ( parent.equals ( getViewSite() ) ) {
                return getChildren ( _repository );
            }
            return getChildren ( parent );
        }
        public Object getParent ( Object child )
        {
            if (child instanceof HiveConnection)
            {
                return _repository;
            }
            else if ( child instanceof HiveItem )
            {
                return ((HiveItem)child).getConnection();
            }
            else if ( child instanceof BrowserEntry )
            {
                return ((BrowserEntry)child).getParent ();
            }
            return null;
        }
        public Object [] getChildren(Object parent)
        {
            if ( parent instanceof HiveRepository )
            {
                return((HiveRepository)parent).getConnections().toArray(new HiveConnection[0]);
            }
            else if ( parent instanceof HiveConnection )
            {
                //return ((HiveConnection)parent).getItemList().toArray(new HiveItem[0]);
                return ((HiveConnection)parent).getRootFolder ().getEntries ();
            }
            else if ( parent instanceof FolderEntry )
            {
                return ((FolderEntry)parent).getEntries ();
            }
            return new Object[0];
        }
        public boolean hasChildren(Object parent)
        {
            if (parent instanceof HiveRepository)
            {
                return ((HiveRepository)parent).getConnections().size() > 0;
            }
            else if ( parent instanceof HiveConnection )
            {
                //return ((HiveConnection)parent).getItemList().size() > 0;
                return ((HiveConnection)parent).getRootFolder ().hasChildren ();
            }
            else if ( parent instanceof FolderEntry )
            {
                return ((FolderEntry)parent).hasChildren ();
            }
            return false;
        }

    }
    class ViewLabelProvider extends LabelProvider
    {
        
        public String getText(Object obj)
        {
            if ( obj instanceof HiveConnection )
            {
                HiveConnection connection = (HiveConnection)obj;
                return connection.getConnectionInformation().getHost() + ":" + connection.getConnectionInformation().getPort() + " (" + connection.getConnection ().getState ().toString () + ")";
            }
            else if ( obj instanceof HiveItem )
            {
                return ((HiveItem)obj).getItemName();
            }
            else if ( obj instanceof BrowserEntry )
            {
                return ((BrowserEntry)obj).getName ();
            }
            return obj.toString();
        }
        public Image getImage(Object obj)
        {
            String imageKey;
            
            if ( obj instanceof HiveConnection )
            {
                HiveConnection connection = (HiveConnection)obj;
                if ( connection.isConnectionRequested() )
                {
                    switch ( connection.getConnection ().getState () )
                    {
                    case CLOSED:
                        imageKey = ISharedImages.IMG_HIVE_DISCONNECTED;
                        break;
                    case CONNECTED:
                        imageKey = ISharedImages.IMG_HIVE_CONNECTION;
                        break;
                    case BOUND:
                        imageKey = ISharedImages.IMG_HIVE_CONNECTED;
                        break;
                    default:
                        imageKey = ISharedImages.IMG_HIVE_DISCONNECTED;
                        break;
                    }
                }
                else
                    imageKey = ISharedImages.IMG_HIVE_CONNECTION;
            }
            else if ( obj instanceof HiveItem )
            {
                HiveItem hiveItem = (HiveItem)obj;
                EnumSet<IODirection> io = hiveItem.getItemInfo ().getIODirection ();
                if ( io.containsAll ( EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) ))
                    imageKey = ISharedImages.IMG_HIVE_ITEM_IO;
                else if ( io.contains ( IODirection.INPUT ) )
                    imageKey = ISharedImages.IMG_HIVE_ITEM_I;
                else if ( io.contains ( IODirection.OUTPUT ) )
                    imageKey = ISharedImages.IMG_HIVE_ITEM_O;
                else
                    imageKey = ISharedImages.IMG_HIVE_ITEM;
            }
            else if ( obj instanceof FolderEntry )
                return PlatformUI.getWorkbench().getSharedImages().getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FOLDER );
            else
                return PlatformUI.getWorkbench().getSharedImages().getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_ELEMENT );
            
            return Openscada_da_client_testPlugin.getDefault().getImageRegistry().get ( imageKey );
        }
    }
    
    class NameSorter extends ViewerSorter
    {
    }
    
    /**
     * The constructor.
     */
    public HiveView()
    {
        _repository = Openscada_da_client_testPlugin.getRepository ();
        _repository.addObserver ( this );
        registerAllConnections ();
    }
    
    @Override
    public void dispose ()
    {
        unregisterAllConnections ();
        _repository.deleteObserver ( this );
        super.dispose ();
    }
    
    public void update ( Observable o, Object arg )
    {
        _log.debug ( "Update: " + o + " / " + arg );
        if ( o == _repository )
        {
            triggerUpdateRepository ( null );
        }
        else if ( o instanceof HiveConnection )
        {
            triggerUpdateRepository ( o );
        }
        else if ( o instanceof FolderEntry )
        {
            if ( (arg != null) && (arg instanceof FolderEntry) )
            {
                refreshForFolder ( (FolderEntry)arg  );
            }
            else
            {
                refreshForFolder ( (FolderEntry)o );
            }
        }
    }
    
    private void refreshForFolder ( FolderEntry folder )
    {
        if ( folder == null )
            return;
        
        if ( folder.getParent () == null )
            triggerUpdateRepository ( folder.getConnection () );
        else
            triggerUpdateRepository ( folder );
    }
    
    public void triggerUpdateRepository ( final Object arg0 )
    {
        if ( !_viewer.getControl ().isDisposed () )
        {
            _viewer.getControl().getDisplay().asyncExec(new Runnable(){

                public void run ()
                {
                    if ( !_viewer.getControl().isDisposed() )
                        performUpdateRepository ( arg0 );
                }});
        }
    }
    
    private void performUpdateRepository ( Object arg0 )
    {
        _log.debug ( "Perform update on: " + arg0 );
        if ( arg0 == null )
        {
            unregisterAllConnections ();
            _viewer.refresh ( true );
            registerAllConnections ();
        }
        else
        {
            _viewer.refresh ( arg0, true );
        }
    }
    
    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    public void createPartControl ( Composite parent )
    {
        _viewer = new TreeViewer ( parent, SWT.H_SCROLL | SWT.V_SCROLL );
        drillDownAdapter = new DrillDownAdapter(_viewer);
        _viewer.setContentProvider ( new ViewContentProvider() );
        _viewer.setLabelProvider ( new ViewLabelProvider() );
        _viewer.setSorter ( new NameSorter() );
        _viewer.setInput ( _repository );
        
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
        
        getSite().setSelectionProvider ( _viewer );
    }
    
    
    
    private void hookContextMenu()
    {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                HiveView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(_viewer.getControl());
        _viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, _viewer);
    }
    
    private void contributeToActionBars()
    {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }
    
    private void fillLocalPullDown(IMenuManager manager)
    {
        //manager.add(connectAction);
        //manager.add(new Separator());
    }
    
    private void fillContextMenu(IMenuManager manager)
    {
        //manager.add(connectAction);
        //manager.add(new Separator());
        //drillDownAdapter.addNavigationActions ( manager );
        // Other plug-ins can contribute there actions here
        manager.add ( new Separator ( IWorkbenchActionConstants.MB_ADDITIONS ) );
    }
    
    private void fillLocalToolBar(IToolBarManager manager)
    {
        //manager.add(connectAction);
        //manager.add(new Separator());
        //drillDownAdapter.addNavigationActions ( manager );
    }
    
    private void makeActions()
    {
        // Connect Action

        /*
        connectAction = new Action() {
            public void run() {
                performConnect();
            }
        };
        connectAction.setText("Connect");
        connectAction.setToolTipText("Establish connection to hive");
        connectAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
        */
        connectAction = new ConnectHiveAction();
    }
    
    private void hookDoubleClickAction()
    {
        _viewer.addDoubleClickListener ( new IDoubleClickListener()
        {
            public void doubleClick ( DoubleClickEvent event )
            {
                connectAction.selectionChanged ( null, event.getSelection () );
                connectAction.run ( null );
            }
        });
    }
   
    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus()
    {
        _viewer.getControl ().setFocus ();
    }
    
    synchronized private void unregisterAllConnections ()
    {
        for ( HiveConnection connection : _repository.getConnections() )
        {
            connection.deleteObserver ( this );
            connection.getRootFolder ().deleteObserver ( this );
        }
    }
    
    synchronized private void registerAllConnections ()
    {
        unregisterAllConnections();
        
        for ( HiveConnection connection : _repository.getConnections() )
        {
            connection.addObserver ( this );
            connection.getRootFolder ().addObserver ( this );
        }
    }
}