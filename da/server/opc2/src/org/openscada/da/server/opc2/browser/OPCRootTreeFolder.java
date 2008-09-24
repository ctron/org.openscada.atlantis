package org.openscada.da.server.opc2.browser;

import java.util.LinkedList;

import org.openscada.da.server.opc2.connection.OPCController;
import org.openscada.da.server.opc2.connection.OPCStateListener;

public class OPCRootTreeFolder extends OPCTreeFolder implements OPCStateListener
{

    private OPCController controller;

    public OPCRootTreeFolder ( OPCController controller )
    {
        super ( controller, new LinkedList<String> () );
        this.controller = controller;
    }
    
    @Override
    public void added ()
    {
        super.added ();
        this.controller.addStateListener ( this );
    }
    
    @Override
    public void removed ()
    {
        this.controller.removeStateListener ( this );
        super.removed ();
    }

    public void connectionEstablished ()
    {
        checkRefresh ();
    }

    public void connectionLost ()
    {
        this.folderImpl.clear ();
        this.refreshed = false;
    }

}
