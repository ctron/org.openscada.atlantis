package org.openscada.ae.client.test;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.ae.core.EventInformation;
import org.openscada.ae.core.Listener;
import org.openscada.core.Variant;

public class DumpListener implements Listener
{
    private static Logger _log = Logger.getLogger ( DumpListener.class );
    
    public void events ( EventInformation[] events )
    {
        _log.debug ( String.format ( "Received events: %d", events.length ) );
        for ( EventInformation event : events )
        {
            _log.debug ( String.format ( "============================================" ) );
            _log.debug ( String.format ( "Action: %1$d", event.getAction () ) );
            _log.debug ( String.format ( "Timestamp: %1$TF %1$TT", event.getTimestamp () ) );
            _log.debug ( String.format ( "\tId: %s", event.getEvent ().getId () ) );
            _log.debug ( String.format ( "\tTimestamp: %1$TF %1$TT", event.getEvent ().getTimestamp () ) );
            for ( Map.Entry<String, Variant> entry : event.getEvent ().getAttributes ().entrySet () )
            {
                _log.debug ( String.format ( "\t\t'%1$s'=>'%2$s'", entry.getKey (), entry.getValue ().toString () ) );
            }
        }
    }

    public void unsubscribed ( String reason )
    {
        _log.debug ( String.format ( "LongRunningListener unsubscribed: %s", reason ) );
    }

}
