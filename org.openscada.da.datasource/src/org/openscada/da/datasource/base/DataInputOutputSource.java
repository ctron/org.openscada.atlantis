package org.openscada.da.datasource.base;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.Variant;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public class DataInputOutputSource extends DataInputSource
{
    private WriteHandler writeHandler;

    public DataInputOutputSource ( final Executor executor )
    {
        super ( executor );
    }

    public DataInputOutputSource ( final Executor executor, WriteHandler writeHandler )
    {
        super ( executor );
        this.writeHandler = writeHandler;
    }

    @Override
    public NotifyFuture<WriteResult> startWriteValue ( final WriteInformation writeInformation, final Variant value )
    {
        final WriteHandler writeHandler = this.writeHandler;

        // if we don't have a write handler this is not allowed
        if ( writeHandler == null )
        {
            return new InstantErrorFuture<WriteResult> ( new InvalidOperationException ().fillInStackTrace () );
        }

        final FutureTask<WriteResult> task = new FutureTask<WriteResult> ( new Callable<WriteResult> () {

            public WriteResult call () throws Exception
            {
                writeHandler.handleWrite ( writeInformation.getUserInformation (), value );
                return new WriteResult ();
            }
        } );

        getExecutor ().execute ( task );

        return task;
    }

    @Override
    public NotifyFuture<WriteAttributeResults> startWriteAttributes ( WriteInformation writeInformation, Map<String, Variant> attributes )
    {
        return new InstantErrorFuture<WriteAttributeResults> ( new InvalidOperationException ().fillInStackTrace () );
    }

    public void setWriteHandler ( WriteHandler writeHandler )
    {
        this.writeHandler = writeHandler;
    }
}
