package org.openscada.ae.ui.testing.views;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.ui.connection.data.ConditionStatusBean;
import org.openscada.core.subscription.SubscriptionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorsView extends AbstractEntryViewPart
{
    private final static Logger logger = LoggerFactory.getLogger ( MonitorsView.class );

    public static final String VIEW_ID = "org.openscada.ae.ui.testing.views.MonitorsView";

    private Label stateLabel;

    private final Map<String, ConditionStatusBean> conditionSet = new HashMap<String, ConditionStatusBean> ();

    private final WritableSet conditions;

    private TableViewer viewer;

    public MonitorsView ()
    {
        this.conditions = new WritableSet ( SWTObservables.getRealm ( Display.getDefault () ) );
    }

    @Override
    public void createPartControl ( final Composite parent )
    {
        final GridLayout layout = new GridLayout ( 1, false );
        layout.horizontalSpacing = layout.verticalSpacing = 0;
        layout.marginHeight = layout.marginWidth = 0;

        parent.setLayout ( layout );

        this.stateLabel = new Label ( parent, SWT.NONE );
        this.stateLabel.setLayoutData ( new GridData ( SWT.FILL, SWT.FILL, true, false ) );

        final Composite wrapper = new Composite ( parent, SWT.NONE );
        wrapper.setLayoutData ( new GridData ( SWT.FILL, SWT.FILL, true, true ) );

        this.viewer = new TableViewer ( wrapper );

        TableColumnLayout tableLayout;
        wrapper.setLayout ( tableLayout = new TableColumnLayout () );

        TableColumn col;

        col = new TableColumn ( this.viewer.getTable (), SWT.NONE );
        col.setText ( "ID" );
        tableLayout.setColumnData ( col, new ColumnWeightData ( 50 ) );

        col = new TableColumn ( this.viewer.getTable (), SWT.NONE );
        col.setText ( "State" );
        tableLayout.setColumnData ( col, new ColumnWeightData ( 50 ) );

        col = new TableColumn ( this.viewer.getTable (), SWT.NONE );
        col.setText ( "Timestamp" );
        tableLayout.setColumnData ( col, new ColumnWeightData ( 100 ) );

        col = new TableColumn ( this.viewer.getTable (), SWT.NONE );
        col.setText ( "Value" );
        tableLayout.setColumnData ( col, new ColumnWeightData ( 50 ) );

        col = new TableColumn ( this.viewer.getTable (), SWT.NONE );
        col.setText ( "Akn User" );
        tableLayout.setColumnData ( col, new ColumnWeightData ( 50 ) );

        col = new TableColumn ( this.viewer.getTable (), SWT.NONE );
        col.setText ( "Akn Timestamp" );
        tableLayout.setColumnData ( col, new ColumnWeightData ( 100 ) );

        this.viewer.getTable ().setLayout ( layout );
        this.viewer.getTable ().setHeaderVisible ( true );

        this.viewer.setContentProvider ( new ObservableSetContentProvider () );
        this.viewer.setLabelProvider ( new LabelProvider ( BeansObservables.observeMaps ( this.conditions, ConditionStatusBean.class, new String[] { "id", ConditionStatusBean.PROP_STATUS, ConditionStatusBean.PROP_STATUS_TIMESTAMP, ConditionStatusBean.PROP_VALUE, ConditionStatusBean.PROP_LAST_AKN_USER, ConditionStatusBean.PROP_LAST_AKN_TIMESTAMP } ) ) );
        this.viewer.setInput ( this.conditions );

        hookContextMenu ();
        addSelectionListener ();
    }

    private void hookContextMenu ()
    {
        final MenuManager menuMgr = new MenuManager ( "#PopupMenu", VIEW_ID );
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

    @Override
    public void setFocus ()
    {
        this.viewer.getControl ().setFocus ();
    }

    @Override
    public void handleDataChanged ( final ConditionStatusInformation[] addedOrUpdated, final String[] removed, final boolean full )
    {
        this.conditions.getRealm ().asyncExec ( new Runnable () {

            public void run ()
            {
                performDataChanged ( addedOrUpdated, removed );
            }
        } );
    }

    @Override
    protected void clear ()
    {
        super.clear ();

        this.conditions.getRealm ().asyncExec ( new Runnable () {

            public void run ()
            {
                MonitorsView.this.conditionSet.clear ();
                MonitorsView.this.conditions.clear ();
                MonitorsView.this.stateLabel.setText ( "<no query selected>" );
            }
        } );
    }

    protected void performDataChanged ( final ConditionStatusInformation[] addedOrUpdated, final String[] removed )
    {
        logger.debug ( "Got data change" );

        try
        {
            Collection<ConditionStatusBean> infos = new LinkedList<ConditionStatusBean> ();
            if ( removed != null )
            {
                for ( final String id : removed )
                {
                    final ConditionStatusBean info = this.conditionSet.remove ( id );
                    if ( info != null )
                    {
                        infos.add ( info );
                    }
                }
            }

            this.conditions.removeAll ( infos );

            infos = new LinkedList<ConditionStatusBean> ();

            if ( addedOrUpdated != null )
            {
                for ( final ConditionStatusInformation info : addedOrUpdated )
                {
                    if ( this.conditionSet.containsKey ( info.getId () ) )
                    {
                        // update
                        final ConditionStatusBean infoBean = this.conditionSet.get ( info.getId () );
                        infoBean.update ( info );
                    }
                    else
                    {
                        // add
                        final ConditionStatusBean infoBean = new ConditionStatusBean ( this.entry.getConnection (), info );
                        this.conditionSet.put ( info.getId (), infoBean );
                        infos.add ( infoBean );
                    }
                }
            }

            this.conditions.addAll ( infos );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to handle data", e );
        }
    }

    @Override
    public void handleStatusChanged ( final SubscriptionState status )
    {
        triggerStateUpdate ( status );
    }

    private void triggerStateUpdate ( final SubscriptionState status )
    {
        if ( this.stateLabel.isDisposed () )
        {
            return;
        }

        this.stateLabel.getDisplay ().asyncExec ( new Runnable () {

            public void run ()
            {
                if ( MonitorsView.this.stateLabel.isDisposed () )
                {
                    return;
                }
                MonitorsView.this.stateLabel.setText ( status.toString () );
            }
        } );
    }
}
