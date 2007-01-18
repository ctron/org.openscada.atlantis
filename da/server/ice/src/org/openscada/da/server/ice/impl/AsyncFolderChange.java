package org.openscada.da.server.ice.impl;

import org.apache.log4j.Logger;

import Ice.LocalException;
import OpenSCADA.DA.Browser.AMI_FolderCallback_folderChanged;

public class AsyncFolderChange extends AMI_FolderCallback_folderChanged
{
    private static Logger _log = Logger.getLogger ( AsyncFolderChange.class );

    private SessionImpl _session = null;
    
    public AsyncFolderChange ( SessionImpl session )
    {
        super ();
        _session = session;
    }
    
    @Override
    public void ice_exception ( LocalException ex )
    {
        _log.debug ( "Failed to notify", ex );
        _session.handleListenerError ();
    }

    @Override
    public void ice_response ()
    {
        // no-op
    }

}
