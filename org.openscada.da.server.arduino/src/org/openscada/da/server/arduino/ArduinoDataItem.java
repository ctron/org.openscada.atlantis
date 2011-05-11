package org.openscada.da.server.arduino;

import java.util.concurrent.Executor;

import org.openscada.core.Variant;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;
import org.openscada.utils.concurrent.NotifyFuture;

public class ArduinoDataItem extends DataItemInputOutputChained
{

    public ArduinoDataItem ( final DataItemInformation information, final Executor executor )
    {
        super ( information, executor );
    }

    @Override
    protected NotifyFuture<WriteResult> startWriteCalculatedValue ( final Variant value, final OperationParameters operationParameters )
    {
        // TODO Auto-generated method stub
        return null;
    }

}
