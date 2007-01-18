package org.openscada.da.client.ice;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.openscada.da.core.Location;
import org.openscada.da.ice.BrowserEntryHelper;

import Ice.Current;
import OpenSCADA.DA.Browser.Entry;
import OpenSCADA.DA.Browser._FolderCallbackDisp;

public class FolderCallbackImpl extends _FolderCallbackDisp
{
    private static Logger _log = Logger.getLogger ( FolderCallbackImpl.class );
    
    private Connection _connection = null;
    
    public FolderCallbackImpl ( Connection connection )
    {
        super ();
        _connection = connection;
    }
    
    public void folderChanged ( String [] location, Entry[] added, String[] removed, boolean full, Current __current )
    {
        _log.debug ( String.format ( "folderChanged - location: %s added: %d removed: %d full: %s", Arrays.deepToString ( location ), added.length, removed.length, full ) );
        _connection.folderChanged ( new Location ( location ), BrowserEntryHelper.fromIce ( added ), removed, full );
    }

}
