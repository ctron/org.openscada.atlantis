package org.openscada.da.core;

import org.openscada.da.core.browser.FolderListener;

public interface Session
{
	public void setListener ( ItemChangeListener listener );
    public void setListener ( ItemListListener listener );
    public void setListener ( FolderListener listener );
}
