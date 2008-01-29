package org.openscada.da.server.common.impl.stats;

import org.apache.log4j.Logger;

public class CounterValue implements Tickable
{
    private static Logger _log = Logger.getLogger ( CounterValue.class );
    
    private long _total = 0;
    private long _lastTickValue = 0;
    private long _lastTimestamp = 0;
    private CounterOutput _output;
    
    public synchronized void add ( long value )
    {
        _total += value;
        _lastTickValue = _lastTickValue + Math.abs ( value );
        _log.debug ( String.format ( "Adding: %s, LastTickValue: %s", value, _lastTickValue ) );
    }
    
    public synchronized void tick ()
    {
        // get now
        long ts = System.currentTimeMillis ();
        
        // get the difference ( in seconds )
        long diff = ( ts - _lastTimestamp ) / 1000;
        _lastTimestamp = ts;
        
        // just in case
        if ( diff == 0 )
        {
            diff = 1;
        }
        
        // we need to do this here ... since otherwise the update call later will
        // increment the counter and setting the tickValue to null will discard
        // this information
        long lastTickValue = _lastTickValue;
        _lastTickValue = 0;
        
        // calculate the average
        double avg = ((double)lastTickValue) / ((double)diff);
        _log.debug ( String.format ( "LastTickValue: %s, Diff: %s, Avg: %s", lastTickValue, diff, avg ) );
        _output.setTickValue ( avg, _total );
    }

    public void setOutput ( CounterOutput output )
    {
        _output = output;
    }
}
