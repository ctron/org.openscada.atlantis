package org.openscada.da.server.opc2.connection;

import org.openscada.da.server.opc2.job.Worker;

public class OPCAsync2IoManager extends OPCIoManager
{

    public OPCAsync2IoManager ( final Worker worker, final OPCModel model, final OPCController controller )
    {
        super ( worker, model, controller );
    }

}
