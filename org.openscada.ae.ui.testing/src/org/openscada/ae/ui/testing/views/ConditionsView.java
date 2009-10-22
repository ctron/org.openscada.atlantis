package org.openscada.ae.ui.testing.views;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.client.ConditionListener;
import org.openscada.ae.client.Connection;
import org.openscada.core.subscription.SubscriptionState;

public class ConditionsView extends ViewPart implements ConditionListener
{

    private final static Logger logger = Logger.getLogger ( ConditionsView.class );

    public static final String VIEW_ID = "org.openscada.ae.ui.testing.views.ConditionsView";

    private Connection connection;

    private Label stateLabel;

    private final Map<String, ConditionStatusBean> conditionSet = new HashMap<String, ConditionStatusBean> ();

    private final WritableSet conditions = new WritableSet ();

    private TableViewer viewer;

    private String queryId;

    public ConditionsView ()
    {
    }

    @Override
    public void createPartControl ( final Composite parent )
    {
        GridLayout layout = new GridLayout ( 1, false );
        layout.horizontalSpacing = layout.verticalSpacing = 0;
        layout.marginHeight = layout.marginWidth = 0;

        parent.setLayout ( layout );

        this.stateLabel = new Label ( parent, SWT.NONE );
        this.stateLabel.setLayoutData ( new GridData ( SWT.FILL, SWT.FILL, true, false ) );

        Composite wrapper = new Composite ( parent, SWT.NONE );
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

    @Override
    public void setFocus ()
    {
        this.viewer.getControl ().setFocus ();
    }

    @Override
    public void dispose ()
    {
        this.connection.setConditionListener ( this.queryId, null );
        super.dispose ();
    }

    public void setConnection ( final Connection connection, final String id )
    {
        this.queryId = id;
        this.connection = connection;
        this.connection.setConditionListener ( id, this );
        logger.info ( "Connection set" );
    }

    public void dataChanged ( final ConditionStatusInformation[] addedOrUpdated, final String[] removed )
    {
        this.conditions.getRealm ().asyncExec ( new Runnable () {

            public void run ()
            {
                performDataChanged ( addedOrUpdated, removed );
            }
        } );
    }

    protected void performDataChanged ( final ConditionStatusInformation[] addedOrUpdated, final String[] removed )
    {
        logger.info ( "Got data change" );
        try
        {
            Collection<ConditionStatusBean> infos = new LinkedList<ConditionStatusBean> ();
            if ( removed != null )
            {
                for ( String id : removed )
                {
                    ConditionStatusBean info = this.conditionSet.remove ( id );
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
                for ( ConditionStatusInformation info : addedOrUpdated )
                {
                    if ( this.conditionSet.containsKey ( info.getId () ) )
                    {
                        // update
                        ConditionStatusBean infoBean = this.conditionSet.get ( info.getId () );
                        infoBean.update ( info );
                    }
                    else
                    {
                        // add
                        ConditionStatusBean infoBean = new ConditionStatusBean ( this.connection, info );
                        this.conditionSet.put ( info.getId (), infoBean );
                        infos.add ( infoBean );
                    }
                }
            }

            this.conditions.addAll ( infos );
        }
        catch ( Throwable e )
        {
            e.printStackTrace ();
        }
    }

    public void statusChanged ( final SubscriptionState status )
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
                if ( ConditionsView.this.stateLabel.isDisposed () )
                {
                    return;
                }
                ConditionsView.this.stateLabel.setText ( status.toString () );
            }
        } );
    }
}
