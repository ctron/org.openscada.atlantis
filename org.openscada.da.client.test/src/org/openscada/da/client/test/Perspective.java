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

package org.openscada.da.client.test;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.openscada.da.client.test.views.realtime.RealTimeList;

public class Perspective implements IPerspectiveFactory
{

    public void createInitialLayout ( final IPageLayout layout )
    {
        layout.setEditorAreaVisible ( false );

        final IFolderLayout folder = layout.createFolder ( "org.openscada.da.client.test.CenterFolder", IPageLayout.TOP, 0.65f, IPageLayout.ID_EDITOR_AREA );
        folder.addPlaceholder ( "*:*" );
        folder.addPlaceholder ( "*" );
        folder.addView ( RealTimeList.VIEW_ID );

        layout.addView ( IPageLayout.ID_PROP_SHEET, IPageLayout.BOTTOM, 0.25f, IPageLayout.ID_EDITOR_AREA );
    }
}
