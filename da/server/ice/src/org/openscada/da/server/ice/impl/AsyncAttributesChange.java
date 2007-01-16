package org.openscada.da.server.ice.impl;

import Ice.LocalException;
import OpenSCADA.DA.AMI_DataCallback_attributesChange;

public class AsyncAttributesChange extends AMI_DataCallback_attributesChange
{
    private SessionImpl _session = null;
    
    public AsyncAttributesChange ( SessionImpl session )
    {
        super ();
        _session = session;
    }
    
    @Override
    public void ice_exception ( LocalException ex )
    {
        _session.handleListenerError ();
    }

    @Override
    public void ice_response ()
    {
    }

}
