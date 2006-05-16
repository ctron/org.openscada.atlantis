package org.openscada.da.client.test;

import org.apache.log4j.Logger;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

    private static Logger _log = Logger.getLogger ( Perspective.class );
    
	public void createInitialLayout(IPageLayout layout) {
        
		String editorArea = layout.getEditorArea();
        _log.debug(editorArea);
        
		layout.setEditorAreaVisible(false);
		
		layout.addStandaloneView("org.openscada.da.client.test.views.HiveView",  true, IPageLayout.LEFT, 0.25f, editorArea);
        //layout.addView("org.openscada.da.client.test.views.HiveView", IPageLayout.LEFT, 0.25f, editorArea);
        //layout.addView("org.openscada.da.client.test.views.DataItemWatchView", IPageLayout.BOTTOM, 0.50f, editorArea);
        
        IFolderLayout folder = layout.createFolder("items", IPageLayout.TOP, 0.5f, editorArea);
		folder.addPlaceholder("org.openscada.da.client.test.views.DataItemWatchView" + ":*");
        
		layout.getViewLayout("org.openscada.da.client.test.views.HiveView").setCloseable(false);
	}
}
