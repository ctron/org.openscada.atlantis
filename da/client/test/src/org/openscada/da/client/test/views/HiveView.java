package org.openscada.da.client.test.views;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.openscada.da.client.test.Openscada_da_client_testPlugin;
import org.openscada.da.client.test.config.HiveConnectionInformation;
import org.openscada.da.client.test.impl.HiveConnection;
import org.openscada.da.client.test.impl.HiveRepository;


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

public class HiveView extends ViewPart 
{
    private static Logger _log = Logger.getLogger ( HiveView.class );
    
    private TreeViewer viewer;
    private DrillDownAdapter drillDownAdapter;
    
    private Action connectAction;
    
    private Action action2;
    private Action doubleClickAction;
    
    private HiveRepository _repository;
    
    private Map<HiveConnection,Observer> _obversers = new HashMap<HiveConnection,Observer>();
    
    
    class ViewContentProvider implements IStructuredContentProvider, 
    ITreeContentProvider{
        
        
        private HiveRepository _repository;
        
        public ViewContentProvider ( HiveRepository repository )
        {
            _repository = repository;
        }
        
        
        public void inputChanged(Viewer v, Object oldInput, Object newInput)
        {
        }
        
        public void dispose()
        {
        }
        
        
        public Object[] getElements(Object parent) {
            if (parent.equals(getViewSite())) {
                return getChildren(_repository);
            }
            return getChildren(parent);
        }
        public Object getParent(Object child) {
            if (child instanceof HiveConnection) {
                return _repository;
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
                return ((HiveConnection)parent).getItemList().toArray(new String[0]);
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
                return ((HiveConnection)parent).getItemList().size() > 0;
            }
            return false;
        }
        
    }
    class ViewLabelProvider extends LabelProvider {
        
        public String getText(Object obj) {
            if ( obj instanceof HiveConnection )
            {
                _log.debug("Checking connection label");
                HiveConnection connection = (HiveConnection)obj;
                String text = "";
                
                if ( connection.isConnected() )
                    text += ">";
                
                text += connection.getConnectionInformation().getHost() + ":" + connection.getConnectionInformation().getPort();
                return text;
            }
            return obj.toString();
        }
        public Image getImage(Object obj)
        {
            String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
            
            if ( obj instanceof HiveConnection )
            {
                imageKey = ISharedImages.IMG_OBJ_FOLDER;
            }
            
            return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
        }
    }
    class NameSorter extends ViewerSorter {
    }
    
    /**
     * The constructor.
     */
    public HiveView()
    {
        _repository = new HiveRepository();
        
        IPath hives = Openscada_da_client_testPlugin.getDefault().getStateLocation().append("hives.xml");
        if ( hives.toFile().canRead() )
            _repository.load(hives);
        else
        {
            HiveConnectionInformation connection = new HiveConnectionInformation();
            connection.setHost("localhost");
            connection.setPort((short)1202);
            _repository.getConnections().add(new HiveConnection(connection));
            _repository.save(hives);
        }
        
        registerAllConnections();
    }
    
    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    public void createPartControl(Composite parent) {
        viewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        drillDownAdapter = new DrillDownAdapter(viewer);
        viewer.setContentProvider(new ViewContentProvider(_repository));
        viewer.setLabelProvider(new ViewLabelProvider());
        viewer.setSorter(new NameSorter());
        viewer.setInput(getViewSite());
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
    }
    
    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                HiveView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }
    
    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }
    
    private void fillLocalPullDown(IMenuManager manager)
    {
        manager.add(connectAction);
        manager.add(new Separator());
        manager.add(action2);
    }
    
    private void fillContextMenu(IMenuManager manager) {
        manager.add(connectAction);
        manager.add(action2);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    
    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(connectAction);
        manager.add(action2);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
    }
    
    private void makeActions()
    {
        // Connect Action
        connectAction = new Action() {
            public void run() {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection)selection).getFirstElement();
                if ( obj instanceof HiveConnection )
                {
                    HiveConnection connection = (HiveConnection)obj;
                    connection.connect();
                }
            }
        };
        connectAction.setText("Connect");
        connectAction.setToolTipText("Establish connection to hive");
        connectAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
        
        // Action 2
        
        action2 = new Action() {
            public void run() {
                showMessage("Action 2 executed");
            }
        };
        action2.setText("Action 2");
        action2.setToolTipText("Action 2 tooltip");
        action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
        doubleClickAction = new Action() {
            public void run() {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection)selection).getFirstElement();
                showMessage("Double-click detected on "+obj.toString());
            }
        };
    }
    
    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }
    private void showMessage(String message) {
        MessageDialog.openInformation(
                viewer.getControl().getShell(),
                "Hive View",
                message);
    }
    
    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        viewer.getControl().setFocus();
    }
    
    private void refreshItem ( final Object o )
    {
        _log.debug("Request refresh");
        
        if ( !viewer.getControl().isDisposed() )
        {
            viewer.getControl().getDisplay().asyncExec(new Runnable(){

                public void run ()
                {
                    if ( !viewer.getControl().isDisposed() )
                    {
                        viewer.refresh(o, true);
                    }
                }});
        }
    }
    
    synchronized private void registerAllConnections ()
    {
        // first unregister
        for ( Map.Entry<HiveConnection,Observer> entry : _obversers.entrySet() )
        {
            entry.getKey().deleteObserver(entry.getValue());
        }
        _obversers.clear();
        
        for ( HiveConnection connection : _repository.getConnections() )
        {
            Observer observer = new Observer () {
                
                public void update ( Observable o, Object arg )
                {
                    refreshItem(o);
                }};
                connection.addObserver(observer);
                _obversers.put(connection, observer);
        }
    }
}