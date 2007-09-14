package org.openscada.da.server.spring;

public class FolderEntry extends Entry
{
    private org.openscada.da.server.browser.common.Folder _folder;

    public org.openscada.da.server.browser.common.Folder getFolder ()
    {
        return _folder;
    }

    public void setFolder ( org.openscada.da.server.browser.common.Folder folder )
    {
        _folder = folder;
    }
}
