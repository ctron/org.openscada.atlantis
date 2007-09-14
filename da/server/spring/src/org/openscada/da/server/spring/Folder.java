package org.openscada.da.server.spring;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.server.browser.common.FolderCommon;
import org.springframework.beans.factory.InitializingBean;

public class Folder extends FolderCommon implements InitializingBean
{
    protected Map<String, Entry> _entries = new HashMap<String, Entry> ();

    public void setFolders ( Map<String, Entry> folders )
    {
        _entries = folders;
    }

    public void afterPropertiesSet () throws Exception
    {
        for ( Map.Entry<String, Entry> entry : _entries.entrySet () )
        {
            if ( entry.getValue () instanceof FolderEntry )
            {
                FolderEntry folderEntry = (FolderEntry)entry.getValue ();
                add ( entry.getKey (), folderEntry.getFolder (), entry.getValue ().getAttributes () );
            }
            else if ( entry.getValue () instanceof DataItemEntry )
            {
                DataItemEntry dataItemEntry = (DataItemEntry)entry.getValue ();
                add ( entry.getKey (), dataItemEntry.getItem (), entry.getValue ().getAttributes () );
            }
            else if ( entry.getValue () instanceof DataItemReferenceEntry )
            {
                DataItemReferenceEntry dataItemReferenceEntry = (DataItemReferenceEntry)entry.getValue ();
                add ( entry.getKey (), dataItemReferenceEntry.getDataItem (), entry.getValue ().getAttributes () );
            }
        }
    }
}
