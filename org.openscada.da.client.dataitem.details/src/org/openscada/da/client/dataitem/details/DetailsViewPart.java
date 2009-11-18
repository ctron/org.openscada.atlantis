/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.client.dataitem.details;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.dataitem.details.part.DetailsPart;
import org.openscada.da.ui.connection.data.DataItemHolder;
import org.openscada.da.ui.connection.data.DataSourceListener;
import org.openscada.da.ui.connection.data.Item;

public class DetailsViewPart extends ViewPart
{

    public static final String VIEW_ID = "org.openscada.da.client.dataitem.details.DetailsViewPart";

    private final Collection<DetailsPart> detailParts = new LinkedList<DetailsPart> ();

    private DataItemHolder dataItem;

    private CTabFolder tabFolder;

    private Label headerLabel;

    private Composite header;

    private Label headerValueLabel;

    private final LocalResourceManager resourceManager = new LocalResourceManager ( JFaceResources.getResources () );

    @Override
    public void createPartControl ( final Composite parent )
    {

        final Composite comp = new Composite ( parent, SWT.NONE );
        comp.setLayout ( new GridLayout ( 1, false ) );

        // createHeader ( comp );

        this.tabFolder = new CTabFolder ( comp, SWT.BOTTOM | SWT.FLAT );
        this.tabFolder.setLayoutData ( new GridData ( SWT.FILL, SWT.FILL, true, true ) );

        try
        {
            for ( final DetailsPartInformation info : getPartInformation () )
            {
                final CTabItem tabItem = new CTabItem ( this.tabFolder, SWT.NONE );
                final Composite parentComposite = new Composite ( this.tabFolder, SWT.NONE );
                parentComposite.setLayout ( new FillLayout () );
                tabItem.setControl ( parentComposite );
                createDetailsPart ( tabItem, parentComposite, info );

            }
        }
        catch ( final CoreException e )
        {
            Activator.getDefault ().getLog ().log ( e.getStatus () );
        }
        if ( !this.detailParts.isEmpty () )
        {
            this.tabFolder.setSelection ( 0 );
        }
    }

    private void createHeader ( final Composite parent )
    {
        this.header = new Composite ( parent, SWT.NONE );
        this.header.setLayoutData ( new GridData ( SWT.FILL, SWT.BEGINNING, true, false ) );
        this.header.setLayout ( new RowLayout ( SWT.HORIZONTAL ) );

        this.headerLabel = new Label ( this.header, SWT.NONE );
        this.headerLabel.setText ( "Data Item: <none>" );

        this.headerValueLabel = new Label ( this.header, SWT.NONE );
    }

    private Collection<DetailsPartInformation> getPartInformation () throws CoreException
    {
        final List<DetailsPartInformation> result = new LinkedList<DetailsPartInformation> ();

        for ( final IConfigurationElement element : Platform.getExtensionRegistry ().getConfigurationElementsFor ( Activator.EXTP_DETAILS_PART ) )
        {
            if ( !"detailsPart".equals ( element.getName () ) )
            {
                continue;
            }
            Object o;
            o = element.createExecutableExtension ( "class" );

            if ( ! ( o instanceof DetailsPart ) )
            {
                throw new CoreException ( new Status ( Status.ERROR, Activator.PLUGIN_ID, "DetailsPart is not of type 'DetailsPart'" ) );
            }

            final DetailsPartInformation info = new DetailsPartInformation ();
            info.setDetailsPart ( (DetailsPart)o );
            info.setLabel ( element.getAttribute ( "name" ) );
            info.setSortKey ( element.getAttribute ( "sortKey" ) );
            result.add ( info );
        }

        Collections.sort ( result, new Comparator<DetailsPartInformation> () {

            public int compare ( final DetailsPartInformation arg0, final DetailsPartInformation arg1 )
            {
                String key1 = arg0.getSortKey ();
                String key2 = arg1.getSortKey ();
                if ( key1 == null )
                {
                    key1 = "";
                }
                if ( key2 == null )
                {
                    key2 = "";
                }

                return key1.compareTo ( key2 );
            }
        } );

        return result;
    }

    private void createDetailsPart ( final CTabItem tabItem, final Composite parent, final DetailsPartInformation info ) throws CoreException
    {
        tabItem.setText ( info.getLabel () );

        info.getDetailsPart ().createPart ( parent );
        this.detailParts.add ( info.getDetailsPart () );
    }

    @Override
    public void dispose ()
    {
        for ( final DetailsPart part : this.detailParts )
        {
            part.dispose ();
        }
        disposeDataItem ();

        this.resourceManager.dispose ();

        super.dispose ();
    }

    @Override
    public void setFocus ()
    {
        this.tabFolder.setFocus ();
    }

    /**
     * set the current data item
     * @param item data item or <code>null</code> if none should be selected
     */
    public void setDataItem ( final Item item )
    {
        disposeDataItem ();

        if ( item != null )
        {

            // this.headerLabel.setText ( String.format ( "Data Item: %s", item.getId () ) );
            // this.headerValueLabel.setText ( "" );

            this.dataItem = new DataItemHolder ( Activator.getDefault ().getBundle ().getBundleContext (), item, new DataSourceListener () {

                public void updateData ( final DataItemValue value )
                {
                    DetailsViewPart.this.updateData ( value );
                }
            } );

            for ( final DetailsPart part : this.detailParts )
            {
                part.setDataItem ( this.dataItem );
            }
        }
        else
        {
            this.headerLabel.setText ( "Data Item: <none>" );
            this.headerValueLabel.setText ( "" );

            // clear
            for ( final DetailsPart part : this.detailParts )
            {
                part.setDataItem ( null );
            }
        }
    }

    protected void updateData ( final DataItemValue value )
    {
        getViewSite ().getShell ().getDisplay ().asyncExec ( new Runnable () {

            public void run ()
            {
                // updateHeader ( value );

                for ( final DetailsPart part : DetailsViewPart.this.detailParts )
                {
                    part.updateData ( value );
                }
            }
        } );

    }

    private void updateHeader ( final DataItemValue value )
    {
        if ( value == null )
        {
            this.headerValueLabel.setText ( "<no value>" );
            return;
        }

        this.headerValueLabel.setText ( value.toString () );

        if ( value.isAlarm () )
        {
            this.header.setForeground ( this.resourceManager.createColor ( new RGB ( 1.0f, 0.0f, 0.0f ) ) );
        }
        else
        {
            this.header.setForeground ( null );
        }

        if ( value.isError () )
        {
            this.header.setBackground ( this.resourceManager.createColor ( new RGB ( 1.0f, 1.0f, 0.0f ) ) );
        }
        else if ( value.isManual () )
        {
            this.header.setBackground ( this.resourceManager.createColor ( new RGB ( 0.0f, 1.0f, 1.0f ) ) );
        }
        else
        {
            this.header.setBackground ( null );
        }
    }

    private void disposeDataItem ()
    {
        if ( this.dataItem != null )
        {
            this.dataItem.dispose ();
            this.dataItem = null;
        }
    }
}
