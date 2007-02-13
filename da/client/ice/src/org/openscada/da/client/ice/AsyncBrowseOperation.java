package org.openscada.da.client.ice;

import org.openscada.da.client.BrowseOperationCallback;
import org.openscada.da.ice.BrowserEntryHelper;

import Ice.LocalException;
import Ice.UserException;
import OpenSCADA.DA.AMI_Hive_browse;
import OpenSCADA.DA.Browser.Entry;

public class AsyncBrowseOperation extends AMI_Hive_browse
{
    private BrowseOperationCallback _callback = null;
    
    public AsyncBrowseOperation ( BrowseOperationCallback callback )
    {
        super ();
        _callback = callback;
    }
    
    @Override
    public void ice_exception ( LocalException ex )
    {
        _callback.error ( ex );
    }

    @Override
    public void ice_exception ( UserException ex )
    {
        _callback.failed ( ex.getMessage () );    
    }

    @Override
    public void ice_response ( Entry[] __ret )
    {
        _callback.complete ( BrowserEntryHelper.fromIce ( __ret ) );
    }
}
