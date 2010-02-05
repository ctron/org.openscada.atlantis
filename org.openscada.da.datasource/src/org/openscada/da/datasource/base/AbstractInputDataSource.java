package org.openscada.da.datasource.base;

import java.util.Map;

import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public abstract class AbstractInputDataSource extends AbstractDataSource implements DataSource
{

    public NotifyFuture<WriteAttributeResults> startWriteAttributes ( final WriteInformation writeInformation, final Map<String, Variant> attributes )
    {
        return new InstantErrorFuture<WriteAttributeResults> ( new OperationException ( "Operation not supported" ) );
    }

    public NotifyFuture<WriteResult> startWriteValue ( final WriteInformation writeInformation, final Variant value )
    {
        return new InstantErrorFuture<WriteResult> ( new OperationException ( "Operation not supported" ) );
    }

}
