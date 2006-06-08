package org.openscada.da.client.test;

import org.apache.log4j.Logger;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.openscada.da.client.test.views.DataItemWatchView;
import org.openscada.da.client.test.views.HiveView;

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
       
        layout.addStandaloneView ( IPageLayout.ID_PROP_SHEET, true, IPageLayout.BOTTOM, 0.25f, IPageLayout.ID_EDITOR_AREA  );
	}
}
