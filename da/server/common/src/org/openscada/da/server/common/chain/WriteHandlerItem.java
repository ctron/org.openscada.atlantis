package org.openscada.da.server.common.chain;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.Variant;
import org.openscada.da.core.server.DataItemInformation;

public class WriteHandlerItem extends DataItemInputOutputChained
{

    private WriteHandler writeHandler;
    
    public WriteHandlerItem ( DataItemInformation di, WriteHandler writeHandler )
    {
        super ( di );
        this.writeHandler = writeHandler;
    }

    public WriteHandlerItem ( String itemId, WriteHandler writeHandler )
    {
        super ( itemId );
        this.writeHandler = writeHandler;
    }
    
    /**
     * Change the write handler
     * <p>
     * The write handler will not be called for the last written value
     * only for the next one.
     * 
     * @param writeHandler the new write handler
     */
    public void setWriteHandler ( WriteHandler writeHandler )
    {
        this.writeHandler = writeHandler;
    }
    
    @Override
    protected void writeCalculatedValue ( Variant value ) throws NotConvertableException, InvalidOperationException
    {
        WriteHandler writeHandler = this.writeHandler;
        
        // if we don't have a write handler this is not allowed
        if ( writeHandler == null )
        {
            throw new InvalidOperationException (); 
        }
        
        try
        {
            writeHandler.handleWrite ( value );
        }
        catch ( NotConvertableException e )
        {
            throw e;
        }
        catch ( InvalidOperationException e )
        {
            throw e;
        }
        catch ( Throwable e )
        {
            // FIXME: should be a separate "write failed" exception
            throw new InvalidOperationException ();
        }
    }

}
