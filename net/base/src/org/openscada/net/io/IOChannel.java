/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.net.io;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.util.HashSet;
import java.util.Set;

public abstract class IOChannel
{
    public abstract SelectableChannel getSelectableChannel ();
    public abstract IOChannelListener getIOChannelListener ();
    
    private long _timeout = 0;
    private long _lastReadEvent = System.currentTimeMillis ();
    
    private Set<IOProcessor> _processorSet = new HashSet<IOProcessor> ();
    
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
            processor.getScheduler ().executeJob ( new Runnable(){
                
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
            synchronized ( _processorSet )
            {
                _processorSet.add ( processor );
            }
            processor.registerConnection ( this, ops );
            if ( _timeout > 0 )
                processor.enableConnectionTimeout ( this );
            else
                processor.disableConnectionTimeout ( this );
                
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
        synchronized ( _processorSet )
        {
            _processorSet.remove ( processor );
        }
        
        processor.unregisterConnection ( this );
        processor.disableConnectionTimeout ( this );
    }
    
    public void setTimeOut ( long timeout )
    {
        _timeout = timeout;
        synchronized ( _processorSet )
        {
            for ( IOProcessor processor : _processorSet )
            {
                if ( timeout > 0 )
                    processor.enableConnectionTimeout ( this );
                else
                    processor.disableConnectionTimeout ( this );
            }
        }
    }
    
    public boolean isTimeOut ()
    {
        if ( _timeout <= 0 )
            return false;
        
        if ( ( System.currentTimeMillis () - _lastReadEvent ) > _timeout )
            return true;
        else
            return false;
    }
    
    public void tickRead ()
    {
        _lastReadEvent = System.currentTimeMillis ();
    }
}
