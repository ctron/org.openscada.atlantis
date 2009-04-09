package org.openscada.da.server.exec2.extractor;

import org.openscada.da.server.exec2.command.ExecutionResult;

public abstract class AbstractReturnCodeExtractor extends AbstractBaseExtractor
{
    public AbstractReturnCodeExtractor ( final String id )
    {
        super ( id );
    }

    protected abstract void handleReturnCode ( final int rc );

    @Override
    protected void doProcess ( final ExecutionResult result ) throws Exception
    {
        final Integer rc = result.getExitValue ();

        if ( rc != null )
        {
            handleReturnCode ( rc );
        }
        else
        {
            setError ( new RuntimeException ( "No return code value" ).fillInStackTrace (), "No return code value" );
        }
    }

}