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
import java.util.LinkedList;

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
import org.openscada.da.base.item.DataItemHolder;
import org.openscada.da.client.DataItem;
import org.openscada.da.client.dataitem.details.part.DetailsPart;

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

        for ( final IConfigurationElement element : Platform.getExtensionRegistry ().getConfigurationElementsFor ( Activator.EXTP_DETAILS_PART ) )
        {
            final CTabItem tabItem = new CTabItem ( this.tabFolder, SWT.NONE );
            final Composite parentComposite = new Composite ( this.tabFolder, SWT.NONE );
            parentComposite.setLayout ( new FillLayout () );
            tabItem.setControl ( parentComposite );
            try
            {
                createDetailsPart ( tabItem, parentComposite, element );
            }
            catch ( final CoreException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace ();
            }
        }
        if ( !this.detailParts.isEmpty () )
        {
            this.tabFolder.setSelection ( 0 );
        }
    }

    private void createDetailsPart ( final CTabItem tabItem, final Composite parent, final IConfigurationElement element ) throws CoreException
    {
        final String name = element.getAttribute ( "name" );

        tabItem.setText ( name );

        final Object o = element.createExecutableExtension ( "class" );
        if ( ! ( o instanceof DetailsPart ) )
        {
            throw new CoreException ( new Status ( Status.ERROR, Activator.PLUGIN_ID, "DetailsPart is not of type 'DetailsPart'" ) );
        }

        final DetailsPart part = (DetailsPart)o;
        part.createPart ( parent );
        this.detailParts.add ( part );
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
    public void setDataItem ( final DataItemHolder item )
    {
        disposeDataItem ();

        if ( item != null )
        {
            this.dataItem = new DataItem ( item.getItemId (), item.getItemManager () );

            for ( final DetailsPart part : this.detailParts )
            {
                part.setDataItem ( item.getConnection (), this.dataItem );
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
