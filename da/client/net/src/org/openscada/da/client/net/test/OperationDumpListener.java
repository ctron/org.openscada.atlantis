package org.openscada.da.client.net.test;

import org.apache.log4j.Logger;
import org.openscada.net.base.LongRunningController.Listener;
import org.openscada.net.base.LongRunningController.State;
import org.openscada.net.base.data.Message;

public class OperationDumpListener implements Listener
{
    private static Logger _log = Logger.getLogger ( OperationDumpListener.class );
    
    public void stateChanged ( State state, Message reply, Throwable error )
    {
        _log.info ( String.format ( "Operation state changed: %s", state.toString () ), error );  
    }

}
