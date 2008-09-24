package org.openscada.da.server.opc2.connection;

import org.openscada.opc.dcom.common.KeyedResult;
import org.openscada.opc.dcom.common.Result;
import org.openscada.opc.dcom.da.OPCITEMDEF;
import org.openscada.opc.dcom.da.OPCITEMRESULT;
import org.openscada.opc.dcom.da.OPCITEMSTATE;
import org.openscada.opc.dcom.da.WriteRequest;

public interface IOListener
{
    public void itemRealized ( String itemId, KeyedResult<OPCITEMDEF, OPCITEMRESULT> entry );

    public void itemUnrealized ( String itemId );

    public void dataWritten ( String itemId, Result<WriteRequest> result, Throwable e );

    public void dataRead ( String itemId, KeyedResult<Integer, OPCITEMSTATE> entry, String errorMessage );
}
