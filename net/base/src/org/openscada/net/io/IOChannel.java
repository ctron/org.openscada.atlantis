package org.openscada.net.io;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;

public abstract class IOChannel
{
    public abstract SelectableChannel getSelectableChannel ();
    public abstract IOChannelListener getIOChannelListener ();
    
    /**
     * Register the channel from inside the IO processor thread
     * @param processor the processor to register
     * @param ops the operation
     */
    public void register ( IOProcessor processor, int ops )
    {
        final IOProcessor processor_ = processor;
        final int ops_ = ops;
        
        try 
        {
            processor.getScheduler().executeJob(new Runnable(){
                
                public void run ()
                {
                    performRegister ( processor_, ops_ );
                }});
        }
        catch ( Exception e )
        {
        }
    }
    
    private void performRegister ( IOProcessor processor, int ops )
    {
        try
        {
            processor.registerConnection ( this, ops );
        }
        catch ( ClosedChannelException e )
        {
        }
    }
    
    /**
     * Perform the unregister operation inside the IO loop thread
     * @param processor The IO processor from which it should unregister
     */
    public void unregister ( IOProcessor processor )
    {
        final IOProcessor processor_ = processor;
        
        try
        {
            processor.getScheduler().executeJob(new Runnable (){

                public void run ()
                {
                    performUnregister ( processor_ );
                }});
        }
        catch ( InterruptedException e )
        {
        }
    }
    
    private void performUnregister ( IOProcessor processor )
    {
        processor.unregisterConnection ( this );
    }
}
