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
import org.openscada.da.client.DataItem;
import org.openscada.da.client.dataitem.details.part.DetailsPart;

public class DetailsViewPart extends ViewPart
{

    public static final String VIEW_ID = "org.openscada.da.client.dataitem.details.DetailsViewPart";

    private Collection<DetailsPart> detailParts = new LinkedList<DetailsPart> ();

    private DataItem dataItem;

    @Override
    public void createPartControl ( Composite parent )
    {
        CTabFolder tabFolder = new CTabFolder (parent, SWT.BOTTOM );
        
        for ( IConfigurationElement element : Platform.getExtensionRegistry ().getConfigurationElementsFor ( Activator.EXTP_DETAILS_PART ) )
        {
            CTabItem tabItem = new CTabItem ( tabFolder, SWT.NONE );
            Composite parentComposite = new Composite ( tabFolder, SWT.NONE );
            parentComposite.setLayout ( new FillLayout () );
            tabItem.setControl ( parentComposite );
            try
            {
                createDetailsPart ( tabItem, parentComposite, element );
            }
            catch ( CoreException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if ( !detailParts.isEmpty () )
        {
            tabFolder.setSelection ( 0 );
        }
    }

    private void createDetailsPart ( CTabItem tabItem, Composite parent, IConfigurationElement element ) throws CoreException
    {
        String name = element.getAttribute ( "name" );
        
        tabItem.setText ( name );
        
        Object o = element.createExecutableExtension ( "class" );
        if ( ! ( o instanceof DetailsPart ) )
        {
            throw new CoreException ( new Status ( Status.ERROR, Activator.PLUGIN_ID, "DetailsPart is not of type 'DetailsPart'" ) );
        }

        DetailsPart part = (DetailsPart)o;
        part.createPart ( parent );
        this.detailParts.add ( part );
    }

    @Override
    public void dispose ()
    {
        for ( DetailsPart part : this.detailParts )
        {
            part.dispose ();
        }
        disposeDataItem ();
        super.dispose ();
    }

    @Override
    public void setFocus ()
    {

    }

    /**
     * set the current data item
     * @param item data item or <code>null</code> if none should be selected
     */
    public void setDataItem ( DataItemHolder item )
    {
        disposeDataItem ();

        if ( item != null )
        {
            this.dataItem = new DataItem ( item.getItemId (), item.getItemManager () );

            for ( DetailsPart part : this.detailParts )
            {
                part.setDataItem ( item.getConnection (), this.dataItem );
            }
        }
        else
        {
            // clear
            for ( DetailsPart part : this.detailParts )
            {
                part.setDataItem ( item.getConnection (), null );
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
