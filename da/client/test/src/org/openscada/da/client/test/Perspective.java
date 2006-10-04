/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

import org.apache.log4j.Logger;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.openscada.da.client.test.views.HiveView;
import org.openscada.da.client.test.views.realtime.RealTimeList;

public class Perspective implements IPerspectiveFactory {

    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger ( Perspective.class );
    
	public void createInitialLayout ( IPageLayout layout )
    {
		layout.setEditorAreaVisible ( false );
		
		//layout.addStandaloneView ( HiveView.VIEW_ID,  true, IPageLayout.LEFT, 0.35f, IPageLayout.ID_EDITOR_AREA );
        layout.addView ( HiveView.VIEW_ID, IPageLayout.LEFT, 0.35f, IPageLayout.ID_EDITOR_AREA );
        layout.getViewLayout ( HiveView.VIEW_ID ).setCloseable ( false );
        
        //layout.addView("org.openscada.da.client.test.views.DataItemWatchView", IPageLayout.BOTTOM, 0.50f, editorArea);
        
        IFolderLayout folder = layout.createFolder ( "org.openscada.da.client.test.CenterFolder", IPageLayout.TOP, 0.65f, IPageLayout.ID_EDITOR_AREA  );
		//folder.addPlaceholder ( DataItemWatchView.VIEW_ID + ":*" );
        folder.addPlaceholder ( "*:*" );
        folder.addPlaceholder ( "*" );
        folder.addView ( RealTimeList.VIEW_ID );
       
        layout.addStandaloneView ( IPageLayout.ID_PROP_SHEET, true, IPageLayout.BOTTOM, 0.25f, IPageLayout.ID_EDITOR_AREA  );
	}
}
