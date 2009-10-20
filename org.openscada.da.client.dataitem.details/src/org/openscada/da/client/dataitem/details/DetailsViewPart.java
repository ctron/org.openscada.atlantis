/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.openscada.da.client.AsyncDataItem;
import org.openscada.da.client.DataItem;
import org.openscada.da.client.base.connection.ConnectionManager;
import org.openscada.da.client.base.item.DataItemHolder;
import org.openscada.da.client.base.item.ItemSelectionHelper;
import org.openscada.da.client.dataitem.details.part.DetailsPart;
import org.openscada.da.ui.connection.data.Item;

public class DetailsViewPart extends ViewPart
{

    public static final String VIEW_ID = "org.openscada.da.client.dataitem.details.DetailsViewPart";

    private final Collection<DetailsPart> detailParts = new LinkedList<DetailsPart> ();

    private DataItem dataItem;

    private CTabFolder tabFolder;

    @Override
    public void createPartControl ( final Composite parent )
    {
        this.tabFolder = new CTabFolder ( parent, SWT.BOTTOM );

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

        final DataItemHolder itemHolder = ItemSelectionHelper.hookupItem ( item.getConnectionString (), item.getId (), ConnectionManager.getDefault () );

        if ( item != null )
        {
            this.dataItem = new AsyncDataItem ( itemHolder.getItemId (), itemHolder.getItemManager () );

            for ( final DetailsPart part : this.detailParts )
            {
                part.setDataItem ( itemHolder, this.dataItem );
            }
        }
        else
        {
            // clear
            for ( final DetailsPart part : this.detailParts )
            {
                part.setDataItem ( null, null );
            }
        }
    }

    private void disposeDataItem ()
    {
        if ( this.dataItem != null )
        {
            this.dataItem.unregister ();
            this.dataItem = null;
        }
    }
}
